import React, { useMemo, useRef, useState } from 'react';
import {
  ScanLine,
  Sparkles,
  Search,
  ArrowDown,
  ArrowUp,
  Trash2,
  Wand2,
} from 'lucide-react';
import { formatSom, TransactionEntity, TransactionType } from '../data/models';
import { KeshThemeSpec, ThemeScene } from '../theme/keshThemes';
import {
  AkatsukiCloudSvg,
  CharacterAddActionIcon,
  ThemeEmblemBadge,
} from './KeshAnimeArt';
import { CategoryBadge, KeshTopHeader } from './KeshBrandComponents';

type FilterTab = 'ALL' | 'EXPENSE' | 'INCOME';

export const OverviewScreen: React.FC<{
  spec: KeshThemeSpec;
  activeScene: ThemeScene | null;
  autoMoodScene: boolean;
  userName: string;
  monthlyBudget: number;
  transactions: TransactionEntity[];
  onOpenThemes: () => void;
  onSelectScene: (sceneId: string) => void;
  onToggleAutoMoodScene: () => void;
  onOpenAdd: () => void;
  onScanReceiptFile: (file: File) => void;
  onOpenAiAssistant: () => void;
  onDeleteTransaction: (tx: TransactionEntity) => void;
}> = ({
  spec,
  activeScene,
  autoMoodScene,
  userName,
  monthlyBudget,
  transactions,
  onOpenThemes,
  onSelectScene,
  onToggleAutoMoodScene,
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

  const companionQuote = useMemo(() => {
    if (budgetRatio >= 0.85) return spec.quotes.alert;
    if (budgetRatio >= 0.45) return spec.quotes.caution;
    return spec.quotes.healthy;
  }, [budgetRatio, spec.quotes]);

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

  const accentTextColor = spec.isLight ? '#FFFFFF' : '#090C14';

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
        {/* Left Column: Character Hero Balance Card, Quick Actions & Scene Companion */}
        <div className="lg:col-span-5 space-y-4">
          <div
            className="rounded-3xl p-5 backdrop-blur-md relative overflow-hidden transition-all"
            style={{
              backgroundColor: spec.surfaceGlass,
              backgroundImage: `linear-gradient(135deg, ${spec.primary}2E 0%, ${spec.surfaceGlass} 60%, ${spec.secondary}26 100%)`,
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
              <ThemeEmblemBadge spec={spec} size={spec.isAnime ? 64 : 48} />
              <div className="min-w-0 flex-1">
                <div className="text-xs" style={{ color: spec.textSecondary }}>
                  Общий баланс
                </div>
                <div
                  className="text-2xl sm:text-3xl font-extrabold tracking-tight tabular-nums mt-0.5"
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
                className="rounded-2xl p-3 flex items-center gap-2.5"
                style={{ backgroundColor: 'rgba(16,185,129,0.14)' }}
              >
                <ArrowDown size={18} color="#10B981" className="shrink-0" />
                <div className="min-w-0">
                  <div className="text-[11px]" style={{ color: 'rgba(16,185,129,0.9)' }}>
                    Доходы
                  </div>
                  <div className="text-xs sm:text-sm font-bold text-emerald-500 truncate tabular-nums">
                    +{formatSom(totalIncome)}
                  </div>
                </div>
              </div>

              <div
                className="rounded-2xl p-3 flex items-center gap-2.5"
                style={{ backgroundColor: 'rgba(239,68,68,0.14)' }}
              >
                <ArrowUp size={18} color="#EF4444" className="shrink-0" />
                <div className="min-w-0">
                  <div className="text-[11px]" style={{ color: 'rgba(239,68,68,0.9)' }}>
                    Расходы
                  </div>
                  <div className="text-xs sm:text-sm font-bold text-red-500 truncate tabular-nums">
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
                className="w-full h-2 rounded-full overflow-hidden"
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

          {/* Quick Actions with Character Crest on Add */}
          <div className="grid grid-cols-3 gap-2.5">
            <button
              type="button"
              onClick={onOpenAdd}
              className="rounded-2xl py-3.5 px-2 flex flex-col items-center justify-center gap-1.5 font-bold text-xs transition-transform active:scale-95 cursor-pointer min-h-[76px]"
              style={{
                backgroundColor: spec.primary,
                color: accentTextColor,
                boxShadow: spec.cardGlow,
              }}
            >
              <CharacterAddActionIcon spec={spec} size={24} />
              <span className="whitespace-nowrap">Добавить</span>
            </button>

            <button
              type="button"
              onClick={() => receiptInputRef.current?.click()}
              className="rounded-2xl py-3.5 px-2 flex flex-col items-center justify-center gap-1.5 font-bold text-xs transition-transform active:scale-95 cursor-pointer min-h-[76px] backdrop-blur-md"
              style={{
                backgroundColor: spec.surfaceGlass,
                color: spec.textPrimary,
                border: `1px solid ${spec.cardBorder}`,
              }}
            >
              <ScanLine size={22} style={{ color: spec.primary }} />
              <span className="whitespace-nowrap">Чек OCR</span>
            </button>

            <button
              type="button"
              onClick={onOpenAiAssistant}
              className="rounded-2xl py-3.5 px-2 flex flex-col items-center justify-center gap-1.5 font-bold text-xs transition-transform active:scale-95 cursor-pointer min-h-[76px] backdrop-blur-md"
              style={{
                backgroundColor: spec.surfaceGlass,
                color: spec.textPrimary,
                border: `1px solid ${spec.cardBorder}`,
              }}
            >
              <Sparkles size={22} style={{ color: spec.primary }} />
              <span className="whitespace-nowrap">ИИ-Чат</span>
            </button>
          </div>

          {/* Character Companion & Interactive Scene Gallery */}
          {spec.isAnime && activeScene && (
            <div
              className="rounded-3xl overflow-hidden backdrop-blur-md transition-all"
              style={{
                backgroundColor: spec.surfaceGlass,
                border: `1px solid ${spec.cardBorder}`,
                boxShadow: spec.cardGlow,
              }}
            >
              <div className="relative h-52 sm:h-60 w-full overflow-hidden">
                <img
                  src={activeScene.image}
                  alt={`${spec.name} — ${activeScene.label}`}
                  referrerPolicy="no-referrer"
                  className="w-full h-full object-cover object-top transition-all duration-500"
                />
                <div
                  className="absolute inset-0"
                  style={{
                    background:
                      'linear-gradient(180deg, rgba(6,8,12,0.18) 0%, rgba(6,8,12,0.48) 52%, rgba(6,8,12,0.92) 100%)',
                  }}
                />

                <div className="absolute top-3 left-3 right-3 flex items-center justify-between gap-2">
                  <span
                    className="rounded-xl px-2.5 py-1 text-[11px] font-bold backdrop-blur-md"
                    style={{
                      backgroundColor: 'rgba(10, 12, 18, 0.72)',
                      color: '#F8FAFC',
                      border: `1px solid ${spec.cardBorder}`,
                    }}
                  >
                    {activeScene.label}
                  </span>

                  <button
                    type="button"
                    onClick={onToggleAutoMoodScene}
                    className="rounded-xl px-2.5 py-1.5 text-[11px] font-bold flex items-center gap-1.5 cursor-pointer transition-colors backdrop-blur-md min-h-[32px]"
                    style={{
                      backgroundColor: autoMoodScene
                        ? spec.primary
                        : 'rgba(10, 12, 18, 0.72)',
                      color: autoMoodScene ? accentTextColor : '#F8FAFC',
                      border: `1px solid ${autoMoodScene ? spec.primary : 'rgba(255,255,255,0.22)'}`,
                    }}
                  >
                    <Wand2 size={13} />
                    <span>Авто по бюджету</span>
                  </button>
                </div>

                <div className="absolute bottom-3 left-3.5 right-3.5">
                  <div
                    className="text-[11px] font-bold mb-0.5"
                    style={{ color: spec.primary }}
                  >
                    {spec.characterTitle}
                  </div>
                  <p className="text-xs font-medium text-slate-100 leading-snug">
                    «{companionQuote}»
                  </p>
                </div>
              </div>

              {spec.scenes.length > 1 && (
                <div className="p-3">
                  <div className="flex items-center justify-between text-[11px] mb-2 px-0.5">
                    <span style={{ color: spec.textSecondary }}>
                      Сцены персонажа ({spec.scenes.length})
                    </span>
                    <span style={{ color: spec.primary }}>
                      {autoMoodScene ? 'Подстраивается под лимит' : 'Выбрана вручную'}
                    </span>
                  </div>
                  <div className="flex items-center gap-2 overflow-x-auto pb-0.5">
                    {spec.scenes.map((scene) => {
                      const isCurrent = scene.id === activeScene.id;
                      return (
                        <button
                          key={scene.id}
                          type="button"
                          onClick={() => onSelectScene(scene.id)}
                          className="group shrink-0 rounded-xl overflow-hidden cursor-pointer transition-all text-left w-20"
                          style={{
                            border: `2px solid ${isCurrent ? spec.primary : 'transparent'}`,
                            boxShadow: isCurrent ? `0 0 12px ${spec.primary}55` : 'none',
                            backgroundColor: spec.background,
                          }}
                        >
                          <img
                            src={scene.image}
                            alt={scene.label}
                            referrerPolicy="no-referrer"
                            className="w-full h-12 object-cover object-top"
                          />
                          <div
                            className="px-1.5 py-1 text-[10px] font-semibold truncate"
                            style={{
                              color: isCurrent ? spec.primary : spec.textSecondary,
                            }}
                          >
                            {scene.label}
                          </div>
                        </button>
                      );
                    })}
                  </div>
                </div>
              )}
            </div>
          )}
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
              className="w-full rounded-2xl pl-10 pr-4 py-3 text-sm outline-none transition-colors backdrop-blur-md"
              style={{
                backgroundColor: spec.surfaceGlass,
                color: spec.textPrimary,
                border: `1px solid ${spec.cardBorder}`,
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
                  className="px-3.5 py-2 rounded-xl text-xs font-semibold whitespace-nowrap shrink-0 transition-colors cursor-pointer min-h-[40px] backdrop-blur-md"
                  style={{
                    backgroundColor: isSelected ? `${spec.primary}38` : spec.surfaceGlass,
                    color: isSelected ? spec.primary : spec.textSecondary,
                    border: `1px solid ${isSelected ? spec.primary : spec.cardBorder}`,
                  }}
                >
                  {f.label}
                </button>
              );
            })}
          </div>

          {filteredList.length === 0 ? (
            <div
              className="rounded-2xl p-8 text-center text-sm backdrop-blur-md"
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
  const amountColor = isIncome ? '#10B981' : spec.textPrimary;

  return (
    <div
      className="rounded-2xl p-3.5 flex items-center gap-3 transition-colors backdrop-blur-md"
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
