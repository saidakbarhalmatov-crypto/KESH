import { CATEGORIES, CategoryId, TransactionType } from '../data/models';

export interface AiParsedTransaction {
  amount: number;
  type: TransactionType;
  category: CategoryId;
  title: string;
  bankName: string;
  replyComment: string;
}

const MODEL_NAME = 'gemini-2.5-flash';

export function sanitizeGeminiApiKey(rawKey: string): string {
  return rawKey.replace(/[\s\u200B-\u200D\uFEFF]/g, '').trim();
}

async function executeGeminiRequest(apiKey: string, payload: unknown): Promise<AiParsedTransaction> {
  const cleanKey = sanitizeGeminiApiKey(apiKey);
  if (!cleanKey) {
    throw new Error('Укажите бесплатный Gemini API ключ в Настройках');
  }
  if (!cleanKey.startsWith('AIza') || cleanKey.length < 25) {
    throw new Error(
      'Неверный формат ключа. В Google AI Studio нажмите иконку «Копировать» рядом с ключом (полный ключ начинается с AIza...).'
    );
  }

  const endpoint = `https://generativelanguage.googleapis.com/v1beta/models/${MODEL_NAME}:generateContent?key=${encodeURIComponent(cleanKey)}`;

  const response = await fetch(endpoint, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json; charset=UTF-8' },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    let apiMessage = '';
    try {
      const errJson = await response.json();
      apiMessage = String(errJson?.error?.message ?? '').trim();
    } catch {
      // ignore non-JSON error body
    }

    const lowerMsg = apiMessage.toLowerCase();
    if (lowerMsg.includes('api key not valid') || lowerMsg.includes('api_key_invalid')) {
      throw new Error(
        'Ключ Gemini недействителен (400). Проверьте в Настройках, что вставлен полный ключ AIza..., без обрезки.'
      );
    }
    if (lowerMsg.includes('location is not supported')) {
      throw new Error(
        'Ошибка API (400): запрос Gemini API недоступен из текущего региона (User location is not supported).'
      );
    }
    throw new Error(
      `Ошибка API (${response.status}): ${apiMessage || 'проверьте ключ Gemini и подключение'}`
    );
  }

  const root = await response.json();
  const rawText: string =
    root?.candidates?.[0]?.content?.parts?.[0]?.text ?? '';

  const cleaned = rawText
    .trim()
    .replace(/^```json/i, '')
    .replace(/^```/i, '')
    .replace(/```$/i, '')
    .trim();

  const parsed = JSON.parse(cleaned);
  const amount = Number(parsed.amount ?? 0);
  if (!Number.isFinite(amount) || amount <= 0) {
    throw new Error('ИИ не смог определить сумму');
  }

  const typeStr = String(parsed.type ?? 'EXPENSE').toUpperCase();
  const type: TransactionType = typeStr === 'INCOME' ? 'INCOME' : 'EXPENSE';

  const catStr = String(parsed.category ?? 'OTHER_EXPENSE').toUpperCase() as CategoryId;
  const category: CategoryId = CATEGORIES[catStr]
    ? catStr
    : type === 'INCOME'
      ? 'OTHER_INCOME'
      : 'OTHER_EXPENSE';

  const catMeta = CATEGORIES[category];
  const title = String(parsed.title ?? '').trim() || catMeta.title;
  const bankName = String(parsed.bankName ?? '').trim() || 'Наличные';
  const replyComment = String(parsed.replyComment ?? '').trim() || 'Готово!';

  return {
    amount,
    type,
    category,
    title,
    bankName,
    replyComment,
  };
}

export async function parseTextCommandWithAi(
  apiKey: string,
  userPrompt: string
): Promise<AiParsedTransaction> {
  const cleanKey = sanitizeGeminiApiKey(apiKey);
  if (!cleanKey) {
    throw new Error('Укажите бесплатный Gemini API ключ в Настройках');
  }

  const systemInstruction = `Ты финансовый ИИ-помощник приложения KESH (валюта: кыргызский сом, KGS).
Разбери фразу пользователя (например: "родители скинули мне денег 800 сомов" или "я купил колу за 87 сомов")
и верни СТРОГО валидный JSON без markdown-обёртки со следующими полями:
{
  "amount": число (например 800.0 или 87.0),
  "type": "INCOME" или "EXPENSE",
  "category": одно из [FOOD, GROCERIES, TRANSPORT, SHOPPING, BILLS, HEALTH, ENTERTAINMENT, TRANSFER, SALARY, CASHBACK, TRANSFER_IN, OTHER_EXPENSE, OTHER_INCOME],
  "title": короткое понятное название (например "От родителей" или "Кола"),
  "bankName": банк/кошелёк если упомянут (MBank, Bakai24, О!Деньги и т.д.) или "Наличные",
  "replyComment": короткий дружелюбный ответ на русском в 1 предложение
}`;

  const body = {
    contents: [
      {
        parts: [{ text: `${systemInstruction}\n\nЗапрос пользователя: ${userPrompt}` }],
      },
    ],
  };

  return executeGeminiRequest(cleanKey, body);
}

export async function parseAudioCommandWithAi(
  apiKey: string,
  base64Audio: string,
  mimeType = 'audio/webm'
): Promise<AiParsedTransaction> {
  const cleanKey = sanitizeGeminiApiKey(apiKey);
  if (!cleanKey) {
    throw new Error('Укажите бесплатный Gemini API ключ в Настройках для голосового ИИ');
  }

  const prompt = `Ты финансовый ИИ-помощник приложения KESH (валюта: кыргызский сом, KGS).
Прослушай голосовое сообщение пользователя на русском языке (например: "родители скинули 800 сомов" или "потратил 150 на такси")
и верни СТРОГО валидный JSON без markdown-обёртки:
{
  "amount": число,
  "type": "INCOME" или "EXPENSE",
  "category": одно из [FOOD, GROCERIES, TRANSPORT, SHOPPING, BILLS, HEALTH, ENTERTAINMENT, TRANSFER, SALARY, CASHBACK, TRANSFER_IN, OTHER_EXPENSE, OTHER_INCOME],
  "title": короткое понятное название операции,
  "bankName": банк/кошелёк если упомянут (MBank, Bakai24, О!Деньги и т.д.) или "Наличные",
  "replyComment": что ты услышал и распознал (в 1 короткое предложение на русском)
}`;

  const normalizedMime = mimeType.split(';')[0].trim() || 'audio/webm';

  const body = {
    contents: [
      {
        parts: [
          { text: prompt },
          {
            inline_data: {
              mime_type: normalizedMime,
              data: base64Audio,
            },
          },
        ],
      },
    ],
  };

  return executeGeminiRequest(cleanKey, body);
}

export async function parseReceiptImageWithAi(
  apiKey: string,
  base64Data: string,
  mimeType = 'image/jpeg'
): Promise<AiParsedTransaction> {
  const cleanKey = sanitizeGeminiApiKey(apiKey);
  if (!cleanKey) {
    throw new Error('Укажите бесплатный Gemini API ключ в Настройках');
  }

  const prompt = `Ты финансовый ИИ-помощник приложения KESH (Кыргызстан, валюта сом / KGS / ₵).
Посмотри на фото чека или скриншот банковского перевода. Игнорируй часы в верхней строке телефона и даты (например 2026).
Найди итоговую сумму операции, определи это доход (INCOME) или расход (EXPENSE), название магазина/получателя и банк.
Верни СТРОГО валидный JSON без markdown-обёртки:
{
  "amount": число,
  "type": "INCOME" или "EXPENSE",
  "category": одно из [FOOD, GROCERIES, TRANSPORT, SHOPPING, BILLS, HEALTH, ENTERTAINMENT, TRANSFER, SALARY, CASHBACK, TRANSFER_IN, OTHER_EXPENSE, OTHER_INCOME],
  "title": название магазина или получателя,
  "bankName": банк (MBank, Bakai24, О!Деньги, Optima24 и т.д.) или "Чек",
  "replyComment": краткое пояснение на русском что найдено на фото
}`;

  const body = {
    contents: [
      {
        parts: [
          { text: prompt },
          {
            inline_data: {
              mime_type: mimeType,
              data: base64Data,
            },
          },
        ],
      },
    ],
  };

  return executeGeminiRequest(cleanKey, body);
}

export function blobToBase64(blob: Blob): Promise<{ base64: string; mimeType: string }> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onerror = () => reject(new Error('Не удалось прочитать аудиозапись'));
    reader.onload = () => {
      const result = String(reader.result ?? '');
      const base64 = result.split(',')[1] ?? '';
      if (!base64) {
        reject(new Error('Пустая аудиозапись'));
        return;
      }
      resolve({ base64, mimeType: blob.type || 'audio/webm' });
    };
    reader.readAsDataURL(blob);
  });
}

export function fileToScaledBase64(file: File): Promise<{ base64: string; dataUrl: string; mimeType: string }> {
  return new Promise((resolve, reject) => {
    const objectUrl = URL.createObjectURL(file);
    const img = new Image();

    const cleanup = () => {
      URL.revokeObjectURL(objectUrl);
    };

    const drawToJpeg = (imageEl: HTMLImageElement) => {
      try {
        const maxSide = 1280;
        const ratio = Math.min(maxSide / imageEl.width, maxSide / imageEl.height, 1);
        const targetW = Math.max(1, Math.round(imageEl.width * ratio));
        const targetH = Math.max(1, Math.round(imageEl.height * ratio));
        const canvas = document.createElement('canvas');
        canvas.width = targetW;
        canvas.height = targetH;
        const ctx = canvas.getContext('2d');
        if (!ctx) {
          cleanup();
          reject(new Error('Не удалось обработать изображение'));
          return;
        }
        ctx.drawImage(imageEl, 0, 0, targetW, targetH);
        const dataUrl = canvas.toDataURL('image/jpeg', 0.85);
        const base64 = dataUrl.split(',')[1] ?? '';
        cleanup();
        resolve({ base64, dataUrl, mimeType: 'image/jpeg' });
      } catch {
        cleanup();
        reject(new Error('Не удалось сжать изображение'));
      }
    };

    img.onload = () => drawToJpeg(img);
    img.onerror = () => {
      const reader = new FileReader();
      reader.onerror = () => {
        cleanup();
        reject(new Error('Не удалось прочитать изображение'));
      };
      reader.onload = () => {
        const fallbackImg = new Image();
        fallbackImg.onerror = () => {
          cleanup();
          reject(new Error('Некорректный формат изображения'));
        };
        fallbackImg.onload = () => drawToJpeg(fallbackImg);
        fallbackImg.src = String(reader.result);
      };
      reader.readAsDataURL(file);
    };

    img.src = objectUrl;
  });
}
