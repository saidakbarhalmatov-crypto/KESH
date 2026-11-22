import { CategoryId, TransactionEntity, TransactionSource, TransactionType, CATEGORIES } from './models';

export interface KeshPrefsState {
  selectedThemeId: string;
  selectedSceneByTheme: Record<string, string>;
  customPhotoByTheme: Record<string, string>;
  userName: string;
  monthlyBudget: number;
  geminiApiKey: string;
}

const PREFS_STORAGE_KEY = 'kesh_prefs_v2';
const TX_STORAGE_KEY = 'kesh_transactions_v2';

export const DEFAULT_PREFS: KeshPrefsState = {
  selectedThemeId: 'itachi',
  selectedSceneByTheme: {},
  customPhotoByTheme: {},
  userName: 'Саидакбар',
  monthlyBudget: 35000,
  geminiApiKey: '',
};

export function loadPrefs(): KeshPrefsState {
  try {
    const raw = localStorage.getItem(PREFS_STORAGE_KEY);
    if (!raw) return DEFAULT_PREFS;
    const parsed = JSON.parse(raw);
    return {
      selectedThemeId:
        typeof parsed.selectedThemeId === 'string'
          ? parsed.selectedThemeId
          : DEFAULT_PREFS.selectedThemeId,
      selectedSceneByTheme:
        parsed.selectedSceneByTheme && typeof parsed.selectedSceneByTheme === 'object'
          ? parsed.selectedSceneByTheme
          : {},
      customPhotoByTheme:
        parsed.customPhotoByTheme && typeof parsed.customPhotoByTheme === 'object'
          ? parsed.customPhotoByTheme
          : {},
      userName:
        typeof parsed.userName === 'string' && parsed.userName.trim()
          ? parsed.userName
          : DEFAULT_PREFS.userName,
      monthlyBudget:
        typeof parsed.monthlyBudget === 'number' && parsed.monthlyBudget > 0
          ? parsed.monthlyBudget
          : DEFAULT_PREFS.monthlyBudget,
      geminiApiKey:
        typeof parsed.geminiApiKey === 'string' ? parsed.geminiApiKey : DEFAULT_PREFS.geminiApiKey,
    };
  } catch {
    return DEFAULT_PREFS;
  }
}

export function savePrefs(prefs: KeshPrefsState): void {
  try {
    localStorage.setItem(PREFS_STORAGE_KEY, JSON.stringify(prefs));
  } catch {
    // ignore storage quota issues
  }
}

export function loadTransactions(): TransactionEntity[] {
  try {
    const raw = localStorage.getItem(TX_STORAGE_KEY);
    if (!raw) return [];
    const parsed = JSON.parse(raw);
    if (!Array.isArray(parsed)) return [];
    return parsed.sort((a, b) => b.timestamp - a.timestamp);
  } catch {
    return [];
  }
}

export function saveTransactions(items: TransactionEntity[]): void {
  try {
    localStorage.setItem(TX_STORAGE_KEY, JSON.stringify(items));
  } catch {
    // ignore storage quota issues
  }
}

export function createTransaction(params: {
  amount: number;
  type: TransactionType;
  category: CategoryId;
  title: string;
  bankName?: string;
  source?: TransactionSource;
  note?: string;
  timestamp?: number;
}): TransactionEntity {
  const catMeta = CATEGORIES[params.category];
  return {
    id: Date.now() + Math.floor(Math.random() * 10000),
    amount: params.amount,
    type: params.type,
    category: params.category,
    title: params.title.trim() || catMeta.title,
    bankName: params.bankName?.trim() || 'Наличные',
    source: params.source ?? 'MANUAL',
    note: params.note ?? '',
    timestamp: params.timestamp ?? Date.now(),
  };
}

export function parseVoiceInput(
  spokenText: string
): { amount: number; type: TransactionType; title: string } | null {
  const cleaned = spokenText.trim();
  if (!cleaned) return null;

  const normalizedNumbers = cleaned.replace(/(\d)\s+(\d)/g, '$1$2');
  const amountMatch = normalizedNumbers.match(/(\d+(?:[.,]\d{1,2})?)/);
  if (!amountMatch) return null;

  const amount = parseFloat(amountMatch[1].replace(',', '.'));
  if (!Number.isFinite(amount) || amount <= 0) return null;

  const lower = cleaned.toLowerCase();
  const incomeTriggers = [
    'прибыль',
    'доход',
    'плюс',
    'зарплата',
    'аванс',
    'получил',
    'получила',
    'заработал',
    'заработала',
    'вернули',
    'кэшбэк',
    'кешбэк',
    'пополнение',
    'зачисление',
  ];
  const isIncome = incomeTriggers.some((t) => lower.includes(t));
  const type: TransactionType = isIncome ? 'INCOME' : 'EXPENSE';

  let title = cleaned
    .replace(/\d[\d\s]*(?:[.,]\d{1,2})?/g, ' ')
    .replace(
      /(^|\s)(прибыль|доход|расход|трата|плюс|минус|потратил|потратила|купил|купила|оплатил|оплатила|получил|получила|заработал|заработала|сомов|сом|kgs)(?=\s|$)/gi,
      ' '
    )
    .replace(/[,.:;!?-]+/g, ' ')
    .replace(/\s+/g, ' ')
    .trim();

  if (title) {
    title = title.charAt(0).toUpperCase() + title.slice(1);
  } else {
    title = isIncome ? 'Доход (голос)' : 'Расход (голос)';
  }

  return { amount, type, title };
}

export function exportTransactionsCsv(items: TransactionEntity[]): void {
  const pad = (n: number) => String(n).padStart(2, '0');
  const formatDate = (ts: number) => {
    const d = new Date(ts);
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
  };

  const lines: string[] = ['Дата;Тип;Категория;Название;Сумма (сом);Кошелёк;Источник'];
  for (const item of items) {
    const dateStr = formatDate(item.timestamp);
    const typeStr = item.type === 'INCOME' ? 'Доход' : 'Расход';
    const catTitle = CATEGORIES[item.category]?.title ?? item.category;
    const amountStr = item.amount.toFixed(2);
    const safeTitle = item.title.replace(/;/g, ',');
    lines.push(`${dateStr};${typeStr};${catTitle};${safeTitle};${amountStr};${item.bankName};${item.source}`);
  }

  const csvContent = '\uFEFF' + lines.join('\n');
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = `KESH_report_${Date.now()}.csv`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}
