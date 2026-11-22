import React, { useMemo, useRef, useState } from 'react';
import {
  ScanLine,
  Sparkles,
  Search,
  ArrowDown,
  ArrowUp,
  Trash2,
} from 'lucide-react';
import { formatSom, TransactionEntity, TransactionType } from '../data/models';
import { KeshThemeSpec } from '../theme/keshThemes';
import {
  AkatsukiCloudSvg,
  CharacterAddActionIcon,
  ThemeEmblemBadge,
} from './KeshAnimeArt';
import { CategoryBadge, KeshTopHeader } from './KeshBrandComponents';

type FilterTab = 'ALL' | 'EXPENSE' | 'INCOME';

export const OverviewScreen: React.FC<{
  spec: KeshThemeSpec;
  userName: string;
  monthlyBudget: number;
  transactions: TransactionEntity[];
  onOpenThemes: () => void;
  onOpenAdd: () => void;
  onScanReceiptFile: (file: File) => void;
  onOpenAiAssistant: () => void;
  onDeleteTransaction: (tx: TransactionEntity) => void;
}> = ({
  spec,
  userName,
  monthlyBudget,
  transactions,
  onOpenThemes,
  onOpenAdd,
  onScanReceiptFile,
  onOpenAiAssistant,
  onDeleteTransaction,
}) => {
  const [selectedFilter, setSelectedFilter] = useState<FilterTab>('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const receiptInputRef = useRef<HTMLInputElement | null>(null);

  const startOfMonth = useMemo(() => {
    const now = new Date();
    return new Date(now.getFullYear(), now.getMonth(), 1, 0, 0, 0, 0).getTime();
  }, []);

  const { totalIncome, totalExpense, monthExpense } = useMemo(() => {
    let inc = 0;
    let exp = 0;
    let mExp = 0;
    for (const tx of transactions) {
      if (tx.type === 'INCOME') {
        inc += tx.amount;
      } else {
        exp += tx.amount;
        if (tx.timestamp >= startOfMonth) {
          mExp += tx.amount;
        }
      }
    }
    return { totalIncome: inc, totalExpense: exp, monthExpense: mExp };
  }, [transactions, startOfMonth]);

  const balance = totalIncome - totalExpense;
  const budgetRatio = Math.min(Math.max(monthExpense / Math.max(monthlyBudget, 1), 0), 1);

  const filteredList = useMemo(() => {
    const q = searchQuery.trim().toLowerCase();
    return transactions.filter((item) => {
      const matchesTab =
        selectedFilter === 'ALL' ||
        (selectedFilter === 'EXPENSE' && item.type === 'EXPENSE') ||
        (selectedFilter === 'INCOME' && item.type === 'INCOME');

      if (!matchesTab) return false;
      if (!q) return true;

      return (
        item.title.toLowerCase().includes(q) ||
        item.bankName.toLowerCase().includes(q) ||
        item.category.toLowerCase().includes(q)
      );
    });
  }, [transactions, selectedFilter, searchQuery]);

  const filters: Array<{ key: FilterTab; label: string }> = [
    { key: 'ALL', label: `Все (${transactions.length})` },
    { key: 'EXPENSE', label: 'Расходы' },
    { key: 'INCOME', label: 'Доходы' },
  ];

  return (
    <div className="relative z-10 pb-28 max-w-5xl mx-auto">
      <KeshTopHeader
        userName={userName}
        spec={spec}
        onOpenThemes={onOpenThemes}
      />

      <input
        ref={receiptInputRef}
        type="file"
        accept="image/*"
        className="hidden"
        onChange={(e) => {
          const file = e.target.files?.[0];
          if (file) {
            onScanReceiptFile(file);
          }
          e.target.value = '';
        }}
      />

      <div className="px-4 sm:px-6 grid grid-cols-1 lg:grid-cols-12 gap-5 items-start">
        {/* Left Column: Frosted iPhone-Glass Balance Card & Quick Actions */}
        <div className="lg:col-span-5 space-y-4">
          <div
            className="rounded-3xl p-5 backdrop-blur-xl relative overflow-hidden transition-all"
            style={{
              backgroundColor: spec.surfaceGlass,
              backgroundImage: `linear-gradient(135deg, ${spec.primary}22 0%, rgba(255,255,255,0.06) 50%, ${spec.secondary}1A 100%)`,
              border: `1px solid ${spec.cardBorder}`,
              boxShadow: spec.cardGlow,
            }}
          >
            {spec.animeType === 'ITACHI' && (
              <div className="absolute -right-2 -top-2 opacity-20 pointer-events-none">
                <AkatsukiCloudSvg size={72} />
              </div>
            )}

            <div className="flex items-center gap-4 relative z-10">
              <ThemeEmblemBadge spec={spec} size={spec.isAnime ? 62 : 48} />
              <div className="min-w-0 flex-1">
                <div className="text-xs font-medium" style={{ color: spec.textSecondary }}>
                  Общий баланс
                </div>
                <div
                  className="text-2xl sm:text-3xl font-extrabold tracking-tight tabular-nums mt-0.5 drop-shadow-sm"
                  style={{ color: spec.textPrimary }}
                >
                  {formatSom(balance)}
                </div>
                <div
                  className="text-[11px] font-semibold truncate mt-0.5"
                  style={{ color: spec.primary }}
                >
                  {spec.motto}
                </div>
              </div>
            </div>

            <div className="mt-4 grid grid-cols-2 gap-3 relative z-10">
              <div
                className="rounded-2xl p-3 flex items-center gap-2.5 backdrop-blur-lg"
                style={{
                  backgroundColor: 'rgba(16, 185, 129, 0.16)',
                  border: '1px solid rgba(16, 185, 129, 0.28)',
                }}
              >
                <ArrowDown size={18} color="#34D399" className="shrink-0" />
                <div className="min-w-0">
                  <div className="text-[11px]" style={{ color: '#A7F3D0' }}>
                    Доходы
                  </div>
                  <div className="text-xs sm:text-sm font-bold text-emerald-300 truncate tabular-nums">
                    +{formatSom(totalIncome)}
                  </div>
                </div>
              </div>

              <div
                className="rounded-2xl p-3 flex items-center gap-2.5 backdrop-blur-lg"
                style={{
                  backgroundColor: 'rgba(239, 68, 68, 0.16)',
                  border: '1px solid rgba(239, 68, 68, 0.28)',
                }}
              >
                <ArrowUp size={18} color="#F87171" className="shrink-0" />
                <div className="min-w-0">
                  <div className="text-[11px]" style={{ color: '#FECACA' }}>
                    Расходы
                  </div>
                  <div className="text-xs sm:text-sm font-bold text-rose-300 truncate tabular-nums">
                    -{formatSom(totalExpense)}
                  </div>
                </div>
              </div>
            </div>

            <div className="mt-4 relative z-10">
              <div className="flex items-center justify-between text-xs mb-1.5">
                <span style={{ color: spec.textSecondary }}>
                  Лимит месяца: <span className="tabular-nums">{formatSom(monthExpense)}</span>
                </span>
                <span className="font-bold tabular-nums" style={{ color: spec.primary }}>
                  из {formatSom(monthlyBudget)}
                </span>
              </div>
              <div
                className="w-full h-2 rounded-full overflow-hidden backdrop-blur-sm"
                style={{ backgroundColor: spec.surfaceVariant }}
              >
                <div
                  className="h-full rounded-full transition-all duration-300"
                  style={{
                    width: `${budgetRatio * 100}%`,
                    backgroundColor: budgetRatio > 0.85 ? '#EF4444' : spec.primary,
                  }}
                />
              </div>
            </div>
          </div>

          {/* Translucent iPhone-style Glass Quick Action Buttons */}
          <div className="grid grid-cols-3 gap-2.5">
            <button
              type="button"
              onClick={onOpenAdd}
              className="rounded-2xl py-3.5 px-2 flex flex-col items-center justify-center gap-1.5 font-bold text-xs transition-transform active:scale-95 cursor-pointer min-h-[76px] backdrop-blur-xl"
              style={{
                backgroundColor: spec.buttonGlass,
                color: '#FFFFFF',
                border: `1px solid ${spec.cardBorder}`,
                boxShadow: spec.cardGlow,
              }}
            >
              <CharacterAddActionIcon spec={spec} size={24} />
              <span className="whitespace-nowrap drop-shadow-sm">Добавить</span>
            </button>

            <button
              type="button"
              onClick={() => receiptInputRef.current?.click()}
              className="rounded-2xl py-3.5 px-2 flex flex-col items-center justify-center gap-1.5 font-bold text-xs transition-transform active:scale-95 cursor-pointer min-h-[76px] backdrop-blur-xl"
              style={{
                backgroundColor: spec.surfaceGlass,
                color: spec.textPrimary,
                border: `1px solid ${spec.cardBorder}`,
                boxShadow: spec.cardGlow,
              }}
            >
              <ScanLine size={22} style={{ color: spec.primary }} />
              <span className="whitespace-nowrap">Чек OCR</span>
            </button>

            <button
              type="button"
              onClick={onOpenAiAssistant}
              className="rounded-2xl py-3.5 px-2 flex flex-col items-center justify-center gap-1.5 font-bold text-xs transition-transform active:scale-95 cursor-pointer min-h-[76px] backdrop-blur-xl"
              style={{
                backgroundColor: spec.surfaceGlass,
                color: spec.textPrimary,
                border: `1px solid ${spec.cardBorder}`,
                boxShadow: spec.cardGlow,
              }}
            >
              <Sparkles size={22} style={{ color: spec.primary }} />
              <span className="whitespace-nowrap">ИИ-Чат</span>
            </button>
          </div>
        </div>

        {/* Right Column: Search, Filters & Transaction Cards */}
        <div className="lg:col-span-7 space-y-3">
          <div className="relative">
            <Search
              size={18}
              className="absolute left-3.5 top-1/2 -translate-y-1/2 pointer-events-none"
              style={{ color: spec.textSecondary }}
            />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Поиск по операциям и категориям..."
              className="w-full rounded-2xl pl-10 pr-4 py-3 text-sm outline-none transition-colors backdrop-blur-xl"
              style={{
                backgroundColor: spec.surfaceGlass,
                color: spec.textPrimary,
                border: `1px solid ${spec.cardBorder}`,
                boxShadow: spec.cardGlow,
              }}
            />
          </div>

          <div className="flex items-center gap-2 overflow-x-auto pb-1">
            {filters.map((f) => {
              const isSelected = selectedFilter === f.key;
              return (
                <button
                  key={f.key}
                  type="button"
                  onClick={() => setSelectedFilter(f.key)}
                  className="px-3.5 py-2 rounded-xl text-xs font-semibold whitespace-nowrap shrink-0 transition-colors cursor-pointer min-h-[40px] backdrop-blur-xl"
                  style={{
                    backgroundColor: isSelected ? `${spec.primary}45` : spec.surfaceGlass,
                    color: isSelected ? '#FFFFFF' : spec.textSecondary,
                    border: `1px solid ${isSelected ? spec.primary : spec.cardBorder}`,
                    boxShadow: spec.cardGlow,
                  }}
                >
                  {f.label}
                </button>
              );
            })}
          </div>

          {filteredList.length === 0 ? (
            <div
              className="rounded-2xl p-6 text-center text-sm backdrop-blur-xl"
              style={{
                backgroundColor: spec.surfaceGlass,
                color: spec.textSecondary,
                border: `1px solid ${spec.cardBorder}`,
                boxShadow: spec.cardGlow,
              }}
            >
              Операций пока нет. Нажмите «Добавить», «Чек OCR» или «ИИ-Чат».
            </div>
          ) : (
            <div className="space-y-2.5">
              {filteredList.map((tx) => (
                <TransactionRowCard
                  key={tx.id}
                  tx={tx}
                  spec={spec}
                  onDelete={() => onDeleteTransaction(tx)}
                />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

const TransactionRowCard: React.FC<{
  tx: TransactionEntity;
  spec: KeshThemeSpec;
  onDelete: () => void;
}> = ({ tx, spec, onDelete }) => {
  const dateStr = useMemo(() => {
    const d = new Date(tx.timestamp);
    return d.toLocaleString('ru-RU', {
      day: '2-digit',
      month: 'short',
      hour: '2-digit',
      minute: '2-digit',
    });
  }, [tx.timestamp]);

  const isIncome: boolean = (tx.type as TransactionType) === 'INCOME';
  const amountColor = isIncome ? '#34D399' : spec.textPrimary;

  return (
    <div
      className="rounded-2xl p-3.5 flex items-center gap-3 transition-colors backdrop-blur-xl"
      style={{
        backgroundColor: spec.surfaceGlass,
        border: `1px solid ${spec.cardBorder}`,
        boxShadow: spec.cardGlow,
      }}
    >
      <CategoryBadge categoryId={tx.category} size={42} />
      <div className="min-w-0 flex-1">
        <div
          className="font-semibold text-sm truncate"
          style={{ color: spec.textPrimary }}
        >
          {tx.title}
        </div>
        <div className="text-xs mt-0.5 truncate" style={{ color: spec.textSecondary }}>
          {tx.bankName} · {dateStr}
        </div>
      </div>

      <div className="flex items-center gap-2 shrink-0">
        <span
          className="font-bold text-sm tabular-nums whitespace-nowrap"
          style={{ color: amountColor }}
        >
          {isIncome ? '+' : '-'}
          {formatSom(tx.amount)}
        </span>
        <button
          type="button"
          onClick={onDelete}
          aria-label="Удалить операцию"
          className="p-2 rounded-xl hover:bg-red-500/15 transition-colors cursor-pointer min-h-[40px] min-w-[40px] flex items-center justify-center"
          style={{ color: spec.textSecondary }}
        >
          <Trash2 size={16} />
        </button>
      </div>
    </div>
  );
};
