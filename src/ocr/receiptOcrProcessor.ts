import { CategoryId, detectCategoryFromText, OcrParsedReceipt, TransactionType } from '../data/models';

function detectSourceLabelFromReceipt(lower: string): string {
  if (lower.includes('bakai') || lower.includes('бакай') || lower.includes('qr.bakai.kg')) return 'Bakai24';
  if (lower.includes('mbank') || lower.includes('мбанк')) return 'MBank';
  if (lower.includes('о!деньги') || lower.includes('o!dengi') || lower.includes('мой кошелек')) return 'О!Деньги';
  if (lower.includes('optima') || lower.includes('оптима')) return 'Optima24';
  if (lower.includes('demir') || lower.includes('демир')) return 'DemirBank';
  if (lower.includes('megapay')) return 'MegaPay';
  return 'Чек / Скриншот';
}

function extractTotalAmount(text: string): number | null {
  const cleaned = text
    .replace(/\b\d{1,2}[./-]\d{1,2}[./-]\d{2,4}\b/g, ' ')
    .replace(/\b\d{1,2}:\d{2}(?::\d{2})?\b/g, ' ')
    .replace(/(?:\+?996|0)\s*\d{3}\s*\d{2,3}\s*\d{2,3}/g, ' ')
    .replace(/\b\d{8,}\b/g, ' ')
    .replace(/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/g, ' ');

  const currencyRegex = /(\d[\d\s]{0,9}(?:[.,]\d{1,2})?)\s*(?:₵|сом|сомов|kgs|с\.)/gi;
  for (const match of cleaned.matchAll(currencyRegex)) {
    const val = parseFloat(match[1].replace(/\s+/g, '').replace(',', '.'));
    if (!Number.isNaN(val) && val >= 0.5 && val <= 10_000_000) {
      return val;
    }
  }

  const labelRegex = /(?:итого|к оплате|всего|сумма|оплата|перевод)\s*:?\s*(\d[\d\s]{0,9}(?:[.,]\d{1,2})?)/gi;
  for (const match of cleaned.matchAll(labelRegex)) {
    const val = parseFloat(match[1].replace(/\s+/g, '').replace(',', '.'));
    if (!Number.isNaN(val) && val >= 0.5 && val <= 10_000_000) {
      return val;
    }
  }

  const decimalMatch = cleaned.match(/\b(\d{1,7}[.,]\d{1,2})\b/);
  if (decimalMatch) {
    const val = parseFloat(decimalMatch[1].replace(',', '.'));
    if (!Number.isNaN(val) && val > 0) return val;
  }

  return null;
}

function extractMerchantTitle(lines: string[], fallbackLabel: string): string {
  const kudaIndex = lines.findIndex((line) => {
    const l = line.toLowerCase();
    return l === 'получатель' || l.startsWith('получатель') || l === 'куда' || l.startsWith('куда');
  });
  if (kudaIndex !== -1 && kudaIndex + 1 < lines.length) {
    const recipient = lines[kudaIndex + 1].trim();
    if (recipient.length >= 2 && !/^\d{1,2}:\d{2}$/.test(recipient)) {
      return recipient;
    }
  }

  const qrLine = lines.find((line) => /qr\.|qr/i.test(line));
  if (qrLine && qrLine.length >= 3 && qrLine.length <= 50) {
    return qrLine;
  }

  return fallbackLabel;
}

export function parseReceiptText(rawText: string, imagePreviewUrl?: string): OcrParsedReceipt {
  const cleanedLines = rawText
    .split(/\r?\n/)
    .map((l) => l.trim())
    .filter((line) => {
      if (!line) return false;
      const looksLikeStatusBar = /^\d{1,2}:\d{2}\b.*/.test(line) && line.length <= 18;
      return !looksLikeStatusBar;
    });

  const normalized = cleanedLines.join('\n');
  const lower = normalized.toLowerCase();

  const detectedSource = detectSourceLabelFromReceipt(lower);
  const isIncome =
    ['пополнение', 'поступило', 'зачислено', 'входящий перевод'].some((k) => lower.includes(k)) &&
    !lower.includes('итого') &&
    !lower.includes('оплата');
  const type: TransactionType = isIncome ? 'INCOME' : 'EXPENSE';

  const amount = extractTotalAmount(normalized);
  const merchant = extractMerchantTitle(cleanedLines, detectedSource);
  const category: CategoryId = detectCategoryFromText(lower, type);

  return {
    amount,
    merchant: normalized ? merchant : 'Чек (введите вручную)',
    bankName: detectedSource,
    category,
    type,
    rawText: normalized || rawText,
    imagePreviewUrl,
  };
}
