import React, { useState } from 'react';
import { CheckCircle2, ScanLine, X } from 'lucide-react';
import {
  CategoryId,
  getCategoriesForType,
  OcrParsedReceipt,
  TransactionType,
} from '../data/models';
import { parseReceiptText } from '../ocr/receiptOcrProcessor';
import { KESH_THEMES, KeshThemeSpec } from '../theme/keshThemes';
import {
  AkatsukiCloudSvg,
  CharacterAddActionIcon,
  KeshLuxuryEmblem,
} from './KeshAnimeArt';

const QUICK_WALLETS = ['Наличные', 'Карта', 'MBank', 'О!Деньги', 'Optima24', 'Bakai24', 'DemirBank'];

export const AddOperationSheet: React.FC<{
  spec: KeshThemeSpec;
  onDismiss: () => void;
  onSave: (
    amount: number,
    type: TransactionType,
    category: CategoryId,
    title: string,
    bank: string
  ) => void;
}> = ({ spec, onDismiss, onSave }) => {
  const [type, setType] = useState<TransactionType>('EXPENSE');
  const [amountText, setAmountText] = useState('');
  const [titleText, setTitleText] = useState('');
  const [selectedWallet, setSelectedWallet] = useState('Наличные');
  const [selectedCategory, setSelectedCategory] = useState<CategoryId>('FOOD');

  const availableCategories = getCategoriesForType(type);
  const accentTextColor = spec.isLight ? '#FFFFFF' : '#090C14';

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const parsed = parseFloat(amountText.replace(',', '.').trim());
    if (Number.isFinite(parsed) && parsed > 0) {
      onSave(parsed, type, selectedCategory, titleText, selectedWallet);
    }
  };

  return (
    <div
      className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4"
      onClick={onDismiss}
    >
      <div
        className="w-full sm:max-w-lg rounded-t-3xl sm:rounded-3xl p-5 max-h-[90vh] overflow-y-auto relative"
        style={{
          backgroundColor: spec.surface,
          border: `1px solid ${spec.cardBorder}`,
          boxShadow: spec.cardGlow,
        }}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2.5">
            <div
              className="w-9 h-9 rounded-xl flex items-center justify-center"
              style={{ backgroundColor: spec.primary, color: accentTextColor }}
            >
              <CharacterAddActionIcon spec={spec} size={22} />
            </div>
            <div>
              <h2 className="text-lg font-bold" style={{ color: spec.textPrimary }}>
                Новая операция
              </h2>
              <p className="text-[11px]" style={{ color: spec.primary }}>
                {spec.characterTitle}
              </p>
            </div>
          </div>
          <button
            type="button"
            onClick={onDismiss}
            aria-label="Закрыть"
            className="p-2 rounded-xl cursor-pointer min-h-[40px] min-w-[40px] flex items-center justify-center"
            style={{ color: spec.textSecondary }}
          >
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-2.5">
            {(['EXPENSE', 'INCOME'] as TransactionType[]).map((itemType) => {
              const isSelected = type === itemType;
              const label = itemType === 'EXPENSE' ? 'Расход' : 'Доход';
              const activeColor = itemType === 'EXPENSE' ? '#EF4444' : '#10B981';
              return (
                <button
                  key={itemType}
                  type="button"
                  onClick={() => {
                    setType(itemType);
                    setSelectedCategory(getCategoriesForType(itemType)[0].id);
                  }}
                  className="py-3 rounded-xl font-bold text-sm cursor-pointer transition-colors min-h-[44px]"
                  style={{
                    backgroundColor: isSelected ? `${activeColor}38` : spec.surfaceVariant,
                    color: isSelected ? activeColor : spec.textSecondary,
                    border: `1px solid ${isSelected ? activeColor : 'transparent'}`,
                  }}
                >
                  {label}
                </button>
              );
            })}
          </div>

          <div>
            <label className="block text-xs mb-1" style={{ color: spec.textSecondary }}>
              Сумма (сом)
            </label>
            <input
              type="text"
              inputMode="decimal"
              value={amountText}
              onChange={(e) => setAmountText(e.target.value)}
              placeholder="0"
              autoFocus
              className="w-full rounded-xl px-3.5 py-3 text-base font-bold outline-none tabular-nums"
              style={{
                backgroundColor: spec.background,
                color: spec.textPrimary,
                border: `1px solid ${spec.cardBorder}`,
              }}
            />
          </div>

          <div>
            <label className="block text-xs mb-1" style={{ color: spec.textSecondary }}>
              Название или комментарий
            </label>
            <input
              type="text"
              value={titleText}
              onChange={(e) => setTitleText(e.target.value)}
              placeholder="Например: Кофе или Пополнение"
              className="w-full rounded-xl px-3.5 py-2.5 text-sm outline-none"
              style={{
                backgroundColor: spec.background,
                color: spec.textPrimary,
                border: `1px solid ${spec.cardBorder}`,
              }}
            />
          </div>

          <div>
            <div className="text-xs font-semibold mb-2" style={{ color: spec.textSecondary }}>
              Счёт / Кошелёк
            </div>
            <div className="flex flex-wrap gap-2">
              {QUICK_WALLETS.map((wallet) => {
                const active = selectedWallet === wallet;
                return (
                  <button
                    key={wallet}
                    type="button"
                    onClick={() => setSelectedWallet(wallet)}
                    className="px-3 py-1.5 rounded-xl text-xs font-semibold cursor-pointer min-h-[36px]"
                    style={{
                      backgroundColor: active ? `${spec.primary}40` : spec.background,
                      color: active ? spec.primary : spec.textSecondary,
                      border: `1px solid ${active ? spec.primary : spec.surfaceVariant}`,
                    }}
                  >
                    {wallet}
                  </button>
                );
              })}
            </div>
          </div>

          <div>
            <div className="text-xs font-semibold mb-2" style={{ color: spec.textSecondary }}>
              Категория
            </div>
            <div className="flex flex-wrap gap-2">
              {availableCategories.map((cat) => {
                const active = selectedCategory === cat.id;
                return (
                  <button
                    key={cat.id}
                    type="button"
                    onClick={() => setSelectedCategory(cat.id)}
                    className="px-3 py-1.5 rounded-xl text-xs font-semibold cursor-pointer min-h-[36px]"
                    style={{
                      backgroundColor: active ? `${cat.colorHex}40` : spec.background,
                      color: active ? cat.colorHex : spec.textSecondary,
                      border: `1px solid ${active ? cat.colorHex : spec.surfaceVariant}`,
                    }}
                  >
                    {cat.title}
                  </button>
                );
              })}
            </div>
          </div>

          <button
            type="submit"
            className="w-full py-3.5 rounded-2xl font-bold text-base cursor-pointer transition-transform active:scale-[0.98] min-h-[50px]"
            style={{
              backgroundColor: spec.primary,
              color: accentTextColor,
            }}
          >
            Сохранить операцию
          </button>
        </form>
      </div>
    </div>
  );
};

export const OcrReviewSheet: React.FC<{
  spec: KeshThemeSpec;
  parsed: OcrParsedReceipt;
  onDismiss: () => void;
  onConfirm: (
    amount: number,
    type: TransactionType,
    category: CategoryId,
    title: string,
    bank: string
  ) => void;
}> = ({ spec, parsed, onDismiss, onConfirm }) => {
  const [amountText, setAmountText] = useState(parsed.amount !== null ? String(parsed.amount) : '');
  const [titleText, setTitleText] = useState(parsed.merchant);
  const [bankText, setBankText] = useState(parsed.bankName);
  const [type, setType] = useState<TransactionType>(parsed.type);
  const [category, setCategory] = useState<CategoryId>(parsed.category);
  const [rawInput, setRawInput] = useState(parsed.rawText);

  const accentTextColor = spec.isLight ? '#FFFFFF' : '#090C14';

  const handleApplyRawText = (txt: string) => {
    setRawInput(txt);
    if (!txt.trim()) return;
    const reParsed = parseReceiptText(txt, parsed.imagePreviewUrl);
    if (reParsed.amount !== null) setAmountText(String(reParsed.amount));
    setTitleText(reParsed.merchant);
    setBankText(reParsed.bankName);
    setType(reParsed.type);
    setCategory(reParsed.category);
  };

  return (
    <div
      className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4"
      onClick={onDismiss}
    >
      <div
        className="w-full sm:max-w-lg rounded-t-3xl sm:rounded-3xl p-5 max-h-[90vh] overflow-y-auto space-y-3.5 relative"
        style={{
          backgroundColor: spec.surface,
          border: `1px solid ${spec.cardBorder}`,
          boxShadow: spec.cardGlow,
        }}
        onClick={(e) => e.stopPropagation()}
      >
        {/* Thematic Akatsuki / Character Header Banner */}
        <div
          className="rounded-2xl p-3.5 flex items-center justify-between relative overflow-hidden"
          style={{
            backgroundColor: spec.background,
            border: `1px solid ${spec.cardBorder}`,
          }}
        >
          {spec.animeType === 'ITACHI' && (
            <div className="absolute right-10 top-1 opacity-25 pointer-events-none flex gap-3">
              <AkatsukiCloudSvg size={42} />
              <AkatsukiCloudSvg size={32} />
            </div>
          )}
          <div className="flex items-center gap-2.5 relative z-10">
            {spec.animeType === 'ITACHI' ? (
              <AkatsukiCloudSvg size={36} />
            ) : (
              <ScanLine size={24} style={{ color: spec.primary }} />
            )}
            <div>
              <h2 className="text-base font-bold" style={{ color: spec.textPrimary }}>
                {spec.receiptTitle}
              </h2>
              <p className="text-[11px]" style={{ color: spec.primary }}>
                {spec.motto}
              </p>
            </div>
          </div>
          <button
            type="button"
            onClick={onDismiss}
            aria-label="Закрыть"
            className="p-2 rounded-xl cursor-pointer min-h-[40px] min-w-[40px] flex items-center justify-center relative z-10"
            style={{ color: spec.textSecondary }}
          >
            <X size={20} />
          </button>
        </div>

        {parsed.imagePreviewUrl && (
          <img
            src={parsed.imagePreviewUrl}
            alt="Загруженный чек"
            referrerPolicy="no-referrer"
            className="w-full max-h-44 object-contain rounded-2xl bg-black/30"
            style={{ border: `1px solid ${spec.cardBorder}` }}
          />
        )}

        <div>
          <label className="block text-xs mb-1" style={{ color: spec.textSecondary }}>
            Распознанная сумма (сом)
          </label>
          <input
            type="text"
            inputMode="decimal"
            value={amountText}
            onChange={(e) => setAmountText(e.target.value)}
            placeholder="Введите сумму"
            className="w-full rounded-xl px-3.5 py-2.5 text-base font-bold outline-none tabular-nums"
            style={{
              backgroundColor: spec.background,
              color: spec.textPrimary,
              border: `1px solid ${spec.cardBorder}`,
            }}
          />
        </div>

        <div>
          <label className="block text-xs mb-1" style={{ color: spec.textSecondary }}>
            Магазин / Получатель
          </label>
          <input
            type="text"
            value={titleText}
            onChange={(e) => setTitleText(e.target.value)}
            className="w-full rounded-xl px-3.5 py-2.5 text-sm outline-none"
            style={{
              backgroundColor: spec.background,
              color: spec.textPrimary,
              border: `1px solid ${spec.cardBorder}`,
            }}
          />
        </div>

        <div>
          <label className="block text-xs mb-1" style={{ color: spec.textSecondary }}>
            Способ оплаты / Источник
          </label>
          <input
            type="text"
            value={bankText}
            onChange={(e) => setBankText(e.target.value)}
            className="w-full rounded-xl px-3.5 py-2.5 text-sm outline-none"
            style={{
              backgroundColor: spec.background,
              color: spec.textPrimary,
              border: `1px solid ${spec.cardBorder}`,
            }}
          />
        </div>

        <div className="flex flex-wrap gap-2">
          {getCategoriesForType(type).map((cat) => {
            const active = category === cat.id;
            return (
              <button
                key={cat.id}
                type="button"
                onClick={() => setCategory(cat.id)}
                className="px-3 py-1.5 rounded-xl text-xs font-semibold cursor-pointer"
                style={{
                  backgroundColor: active ? `${cat.colorHex}40` : spec.background,
                  color: active ? cat.colorHex : spec.textSecondary,
                  border: `1px solid ${active ? cat.colorHex : spec.surfaceVariant}`,
                }}
              >
                {cat.title}
              </button>
            );
          })}
        </div>

        <div>
          <label className="block text-xs mb-1" style={{ color: spec.textSecondary }}>
            Текст чека (вставьте текст квитанции для авто-разбора)
          </label>
          <textarea
            rows={2}
            value={rawInput}
            onChange={(e) => handleApplyRawText(e.target.value)}
            placeholder="Например: Квитанция Итого: 450 сом Получатель: Глобус"
            className="w-full rounded-xl p-2.5 text-xs outline-none resize-none"
            style={{
              backgroundColor: spec.background,
              color: spec.textPrimary,
              border: `1px solid ${spec.cardBorder}`,
            }}
          />
        </div>

        <button
          type="button"
          onClick={() => {
            const amount = parseFloat(amountText.replace(',', '.').trim());
            if (Number.isFinite(amount) && amount > 0) {
              onConfirm(amount, type, category, titleText, bankText);
            }
          }}
          className="w-full py-3.5 rounded-2xl font-bold text-sm cursor-pointer min-h-[48px]"
          style={{
            backgroundColor: spec.primary,
            color: accentTextColor,
          }}
        >
          Подтвердить и записать
        </button>
      </div>
    </div>
  );
};

export const ThemePickerSheet: React.FC<{
  currentThemeId: string;
  selectedSceneByTheme?: Record<string, string>;
  spec: KeshThemeSpec;
  onSelectTheme: (id: string) => void;
  onDismiss: () => void;
}> = ({ currentThemeId, selectedSceneByTheme, spec, onSelectTheme, onDismiss }) => {
  const animeThemes = KESH_THEMES.filter((t) => t.isAnime);
  const baseThemes = KESH_THEMES.filter((t) => !t.isAnime);

  return (
    <div
      className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4"
      onClick={onDismiss}
    >
      <div
        className="w-full sm:max-w-lg rounded-t-3xl sm:rounded-3xl p-5 max-h-[88vh] overflow-y-auto space-y-4"
        style={{
          backgroundColor: spec.surface,
          border: `1px solid ${spec.cardBorder}`,
          boxShadow: spec.cardGlow,
        }}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between">
          <h2 className="text-xl font-bold" style={{ color: spec.textPrimary }}>
            Темы оформления KESH
          </h2>
          <button
            type="button"
            onClick={onDismiss}
            aria-label="Закрыть"
            className="p-2 rounded-xl cursor-pointer min-h-[40px] min-w-[40px] flex items-center justify-center"
            style={{ color: spec.textSecondary }}
          >
            <X size={20} />
          </button>
        </div>

        <div className="space-y-2.5">
          <div className="text-xs font-bold" style={{ color: spec.primary }}>
            Аниме-коллекция персонажей ({animeThemes.length})
          </div>
          {animeThemes.map((item) => {
            const isSelected = item.id === currentThemeId;
            const activeSceneId = selectedSceneByTheme?.[item.id] ?? item.scenes[0]?.id;
            const previewBg =
              item.scenes.find((s) => s.id === activeSceneId)?.image ?? item.backgroundImage;

            return (
              <button
                key={item.id}
                type="button"
                onClick={() => onSelectTheme(item.id)}
                className="w-full rounded-2xl p-3.5 text-left cursor-pointer transition-transform active:scale-[0.99] relative overflow-hidden space-y-2.5"
                style={{
                  backgroundColor: item.background,
                  border: `${isSelected ? 2 : 1}px solid ${
                    isSelected ? item.primary : item.cardBorder
                  }`,
                  boxShadow: isSelected ? item.cardGlow : 'none',
                }}
              >
                {previewBg && (
                  <img
                    src={previewBg}
                    alt={item.name}
                    referrerPolicy="no-referrer"
                    className="absolute inset-0 w-full h-full object-cover object-top opacity-25 pointer-events-none"
                  />
                )}
                <div className="flex items-center justify-between gap-3 relative z-10">
                  <div className="flex items-center gap-3 min-w-0">
                    <KeshLuxuryEmblem spec={item} size={42} />
                    <div className="min-w-0">
                      <div className="font-bold text-sm" style={{ color: item.textPrimary }}>
                        {item.name}
                      </div>
                      <div className="text-xs truncate" style={{ color: item.textSecondary }}>
                        {item.subtitle}
                      </div>
                    </div>
                  </div>
                  {isSelected && (
                    <CheckCircle2
                      size={20}
                      className="shrink-0"
                      style={{ color: item.primary }}
                    />
                  )}
                </div>

                {item.scenes.length > 0 && (
                  <div className="flex items-center gap-1.5 relative z-10">
                    {item.scenes.slice(0, 4).map((sc) => {
                      const isSceneCurrent = sc.id === activeSceneId;
                      return (
                        <div
                          key={sc.id}
                          className="w-11 h-8 rounded-lg overflow-hidden shrink-0"
                          style={{
                            border: `1.5px solid ${
                              isSceneCurrent ? item.primary : 'rgba(255,255,255,0.18)'
                            }`,
                          }}
                        >
                          <img
                            src={sc.image}
                            alt={`${item.name} — ${sc.label}`}
                            referrerPolicy="no-referrer"
                            className="w-full h-full object-cover object-top"
                          />
                        </div>
                      );
                    })}
                    <span
                      className="text-[11px] font-semibold ml-1 truncate"
                      style={{ color: item.textSecondary }}
                    >
                      {item.scenes.length} сцены
                    </span>
                  </div>
                )}
              </button>
            );
          })}
        </div>

        <div className="space-y-2">
          <div className="text-xs font-bold" style={{ color: spec.primary }}>
            Базовые темы ({baseThemes.length})
          </div>
          {baseThemes.map((item) => {
            const isSelected = item.id === currentThemeId;
            return (
              <button
                key={item.id}
                type="button"
                onClick={() => onSelectTheme(item.id)}
                className="w-full rounded-2xl p-3.5 flex items-center justify-between gap-3 text-left cursor-pointer transition-transform active:scale-[0.99]"
                style={{
                  backgroundColor: item.background,
                  border: `${isSelected ? 2 : 1}px solid ${
                    isSelected ? item.primary : item.surfaceVariant
                  }`,
                }}
              >
                <div className="flex items-center gap-3 min-w-0">
                  <KeshLuxuryEmblem spec={item} size={38} />
                  <div className="min-w-0">
                    <div className="font-bold text-sm" style={{ color: item.textPrimary }}>
                      {item.name}
                    </div>
                    <div className="text-xs truncate" style={{ color: item.textSecondary }}>
                      {item.subtitle}
                    </div>
                  </div>
                </div>
                {isSelected && <CheckCircle2 size={20} style={{ color: item.primary }} />}
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
};
