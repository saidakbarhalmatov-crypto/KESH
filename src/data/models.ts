export type TransactionType = 'EXPENSE' | 'INCOME';

export type CategoryId =
  | 'FOOD'
  | 'GROCERIES'
  | 'TRANSPORT'
  | 'SHOPPING'
  | 'BILLS'
  | 'HEALTH'
  | 'ENTERTAINMENT'
  | 'TRANSFER'
  | 'OTHER_EXPENSE'
  | 'SALARY'
  | 'FREELANCE'
  | 'TRANSFER_IN'
  | 'CASHBACK'
  | 'OTHER_INCOME';

export interface CategoryMeta {
  id: CategoryId;
  title: string;
  isIncome: boolean;
  colorHex: string;
}

export const CATEGORIES: Record<CategoryId, CategoryMeta> = {
  FOOD: { id: 'FOOD', title: 'Еда и кафе', isIncome: false, colorHex: '#F97316' },
  GROCERIES: { id: 'GROCERIES', title: 'Продукты', isIncome: false, colorHex: '#22C55E' },
  TRANSPORT: { id: 'TRANSPORT', title: 'Транспорт и такси', isIncome: false, colorHex: '#38BDF8' },
  SHOPPING: { id: 'SHOPPING', title: 'Покупки', isIncome: false, colorHex: '#A855F7' },
  BILLS: { id: 'BILLS', title: 'Связь и ЖКХ', isIncome: false, colorHex: '#EAB308' },
  HEALTH: { id: 'HEALTH', title: 'Здоровье', isIncome: false, colorHex: '#EF4444' },
  ENTERTAINMENT: { id: 'ENTERTAINMENT', title: 'Развлечения', isIncome: false, colorHex: '#EC4899' },
  TRANSFER: { id: 'TRANSFER', title: 'Переводы', isIncome: false, colorHex: '#64748B' },
  OTHER_EXPENSE: { id: 'OTHER_EXPENSE', title: 'Прочее', isIncome: false, colorHex: '#94A3B8' },

  SALARY: { id: 'SALARY', title: 'Зарплата', isIncome: true, colorHex: '#10B981' },
  FREELANCE: { id: 'FREELANCE', title: 'Фриланс / Бизнес', isIncome: true, colorHex: '#06B6D4' },
  TRANSFER_IN: { id: 'TRANSFER_IN', title: 'Пополнение', isIncome: true, colorHex: '#3B82F6' },
  CASHBACK: { id: 'CASHBACK', title: 'Кэшбэк / Бонус', isIncome: true, colorHex: '#D4AF37' },
  OTHER_INCOME: { id: 'OTHER_INCOME', title: 'Другой доход', isIncome: true, colorHex: '#84CC16' },
};

export const CATEGORY_LIST: CategoryMeta[] = Object.values(CATEGORIES);

export function getCategoriesForType(type: TransactionType): CategoryMeta[] {
  const wantIncome = type === 'INCOME';
  return CATEGORY_LIST.filter((c) => c.isIncome === wantIncome);
}

export function detectCategoryFromText(lowerText: string, type: TransactionType): CategoryId {
  if (type === 'INCOME') {
    if (lowerText.includes('зарплат') || lowerText.includes('аванс')) return 'SALARY';
    if (lowerText.includes('кэшбэк') || lowerText.includes('кешбэк') || lowerText.includes('бонус')) return 'CASHBACK';
    if (lowerText.includes('перевод') || lowerText.includes('пополнение')) return 'TRANSFER_IN';
    return 'OTHER_INCOME';
  }

  if (['глобус', 'народный', 'фрунзе', 'супермаркет', 'маркет', 'продукт', 'cash-in'].some((k) => lowerText.includes(k))) {
    return 'GROCERIES';
  }
  if (['кафе', 'кофе', 'coffee', 'ресторан', 'столовая', 'бургер', 'шаурма', 'еда', 'food'].some((k) => lowerText.includes(k))) {
    return 'FOOD';
  }
  if (['такси', 'яндекс', 'маршрут', 'тулпар', 'азс', 'бензин', 'транспорт', 'qr'].some((k) => lowerText.includes(k))) {
    return 'TRANSPORT';
  }
  if (['wildberries', 'ozon', 'магазин', 'одежда', 'техника', 'shop'].some((k) => lowerText.includes(k))) {
    return 'SHOPPING';
  }
  if (['свет', 'электричеств', 'коммунал', 'интернет', 'связь', 'мегаком', 'о!', 'билайн'].some((k) => lowerText.includes(k))) {
    return 'BILLS';
  }
  if (['аптека', 'больниц', 'клиник', 'врач', 'health'].some((k) => lowerText.includes(k))) {
    return 'HEALTH';
  }
  if (['кино', 'игры', 'steam', 'playstation', 'подписк', 'youtube', 'spotify'].some((k) => lowerText.includes(k))) {
    return 'ENTERTAINMENT';
  }
  if (['перевод', 'с2с', 'p2p'].some((k) => lowerText.includes(k))) {
    return 'TRANSFER';
  }
  return 'OTHER_EXPENSE';
}

export type TransactionSource = 'MANUAL' | 'AI_ASSISTANT' | 'OCR_RECEIPT';

export interface TransactionEntity {
  id: number;
  amount: number;
  type: TransactionType;
  category: CategoryId;
  title: string;
  bankName: string;
  note: string;
  source: TransactionSource;
  timestamp: number;
}

export interface OcrParsedReceipt {
  amount: number | null;
  merchant: string;
  bankName: string;
  category: CategoryId;
  type: TransactionType;
  rawText: string;
  imagePreviewUrl?: string;
}

export function formatSom(amount: number): string {
  const isWhole = Math.abs(amount % 1) < 0.0001;
  const fixed = isWhole ? Math.round(amount).toString() : amount.toFixed(2);
  const [intPart, decPart] = fixed.split('.');
  const formattedInt = intPart.replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
  return decPart ? `${formattedInt}.${decPart} сом` : `${formattedInt} сом`;
}
