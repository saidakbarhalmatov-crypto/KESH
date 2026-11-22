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

async function executeGeminiRequest(apiKey: string, payload: unknown): Promise<AiParsedTransaction> {
  const endpoint = `https://generativelanguage.googleapis.com/v1beta/models/${MODEL_NAME}:generateContent?key=${encodeURIComponent(apiKey.trim())}`;

  const response = await fetch(endpoint, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json; charset=UTF-8' },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new Error(`Ошибка API (${response.status}): проверьте ключ Gemini`);
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
  if (!apiKey.trim()) {
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

  return executeGeminiRequest(apiKey, body);
}

export async function parseReceiptImageWithAi(
  apiKey: string,
  base64Data: string,
  mimeType = 'image/jpeg'
): Promise<AiParsedTransaction> {
  if (!apiKey.trim()) {
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

  return executeGeminiRequest(apiKey, body);
}

export function fileToScaledBase64(file: File): Promise<{ base64: string; dataUrl: string; mimeType: string }> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onerror = () => reject(new Error('Не удалось прочитать изображение'));
    reader.onload = () => {
      const img = new Image();
      img.onerror = () => reject(new Error('Некорректный формат изображения'));
      img.onload = () => {
        const maxSide = 1280;
        const ratio = Math.min(maxSide / img.width, maxSide / img.height, 1);
        const targetW = Math.max(1, Math.round(img.width * ratio));
        const targetH = Math.max(1, Math.round(img.height * ratio));
        const canvas = document.createElement('canvas');
        canvas.width = targetW;
        canvas.height = targetH;
        const ctx = canvas.getContext('2d');
        if (!ctx) {
          reject(new Error('Не удалось обработать изображение'));
          return;
        }
        ctx.drawImage(img, 0, 0, targetW, targetH);
        const dataUrl = canvas.toDataURL('image/jpeg', 0.85);
        const base64 = dataUrl.split(',')[1] ?? '';
        resolve({ base64, dataUrl, mimeType: 'image/jpeg' });
      };
      img.src = String(reader.result);
    };
    reader.readAsDataURL(file);
  });
}
