import React, { useEffect, useState } from 'react';
import { Download, Palette, Trash2 } from 'lucide-react';
import { KeshThemeSpec } from '../theme/keshThemes';
import { AkatsukiCloudSvg, KeshLuxuryEmblem } from './KeshAnimeArt';
import { KeshTopHeader } from './KeshBrandComponents';

export const SettingsScreen: React.FC<{
  spec: KeshThemeSpec;
  userName: string;
  monthlyBudget: number;
  geminiApiKey: string;
  onUpdateSettings: (name: string, budget: number, apiKey: string) => void;
  onOpenThemes: () => void;
  onExportCsv: () => void;
  onClearAll: () => void;
}> = ({
  spec,
  userName,
  monthlyBudget,
  geminiApiKey,
  onUpdateSettings,
  onOpenThemes,
  onExportCsv,
  onClearAll,
}) => {
  const [nameInput, setNameInput] = useState(userName);
  const [budgetInput, setBudgetInput] = useState(
    monthlyBudget % 1 === 0 ? String(Math.round(monthlyBudget)) : String(monthlyBudget)
  );
  const [apiKeyInput, setApiKeyInput] = useState(geminiApiKey);
  const [savedNotice, setSavedNotice] = useState(false);
  const [showClearDialog, setShowClearDialog] = useState(false);

  useEffect(() => {
    setNameInput(userName);
    setBudgetInput(monthlyBudget % 1 === 0 ? String(Math.round(monthlyBudget)) : String(monthlyBudget));
    setApiKeyInput(geminiApiKey);
  }, [userName, monthlyBudget, geminiApiKey]);

  const accentTextColor = spec.isLight ? '#FFFFFF' : '#090C14';

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    const parsedBudget = parseFloat(budgetInput);
    onUpdateSettings(
      nameInput.trim() || 'Саидакбар',
      Number.isFinite(parsedBudget) && parsedBudget > 0 ? parsedBudget : monthlyBudget,
      apiKeyInput.trim()
    );
    setSavedNotice(true);
    setTimeout(() => setSavedNotice(false), 2500);
  };

  return (
    <div className="relative z-10 pb-28 max-w-4xl mx-auto">
      <KeshTopHeader userName={userName} spec={spec} onOpenThemes={onOpenThemes} />

      <div className="px-4 sm:px-6 space-y-4">
        <h1 className="text-2xl font-extrabold" style={{ color: spec.textPrimary }}>
          Настройки
        </h1>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 items-start">
          {/* Profile, AI & Budget */}
          <form
            onSubmit={handleSave}
            className="rounded-3xl p-5 space-y-3 backdrop-blur-md relative overflow-hidden"
            style={{
              backgroundColor: spec.surfaceGlass,
              border: `1px solid ${spec.cardBorder}`,
              boxShadow: spec.cardGlow,
            }}
          >
            {spec.animeType === 'ITACHI' && (
              <div className="absolute -right-3 -top-2 opacity-15 pointer-events-none">
                <AkatsukiCloudSvg size={64} />
              </div>
            )}
            <h2 className="font-bold text-base relative z-10" style={{ color: spec.textPrimary }}>
              Профиль, ИИ и бюджет
            </h2>

            <div>
              <label className="block text-xs mb-1" style={{ color: spec.textSecondary }}>
                Имя пользователя
              </label>
              <input
                type="text"
                value={nameInput}
                onChange={(e) => setNameInput(e.target.value)}
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
                Месячный лимит расходов (сом)
              </label>
              <input
                type="number"
                inputMode="decimal"
                value={budgetInput}
                onChange={(e) => setBudgetInput(e.target.value)}
                className="w-full rounded-xl px-3.5 py-2.5 text-sm outline-none tabular-nums"
                style={{
                  backgroundColor: spec.background,
                  color: spec.textPrimary,
                  border: `1px solid ${spec.cardBorder}`,
                }}
              />
            </div>

            <div>
              <label className="block text-xs mb-1" style={{ color: spec.textSecondary }}>
                Бесплатный Gemini API ключ
              </label>
              <input
                type="password"
                value={apiKeyInput}
                onChange={(e) => setApiKeyInput(e.target.value)}
                placeholder="AIza..."
                className="w-full rounded-xl px-3.5 py-2.5 text-sm outline-none"
                style={{
                  backgroundColor: spec.background,
                  color: spec.textPrimary,
                  border: `1px solid ${spec.cardBorder}`,
                }}
              />
            </div>

            <button
              type="submit"
              className="w-full py-3 rounded-2xl font-bold text-sm transition-transform active:scale-[0.98] cursor-pointer min-h-[44px]"
              style={{
                backgroundColor: spec.primary,
                color: accentTextColor,
              }}
            >
              {savedNotice ? 'Настройки сохранены' : 'Сохранить настройки'}
            </button>
          </form>

          <div className="space-y-4">
            {/* Themes */}
            <div
              className="rounded-3xl p-5 flex items-center justify-between gap-4 backdrop-blur-md"
              style={{
                backgroundColor: spec.surfaceGlass,
                border: `1px solid ${spec.cardBorder}`,
                boxShadow: spec.cardGlow,
              }}
            >
              <div className="flex items-center gap-3 min-w-0">
                <KeshLuxuryEmblem spec={spec} size={40} />
                <div className="min-w-0">
                  <h2 className="font-bold text-base" style={{ color: spec.textPrimary }}>
                    Оформление и темы
                  </h2>
                  <p className="text-xs mt-0.5 truncate" style={{ color: spec.textSecondary }}>
                    Активна: {spec.name} · {spec.motto}
                  </p>
                </div>
              </div>
              <button
                type="button"
                onClick={onOpenThemes}
                className="flex items-center gap-2 px-4 py-2.5 rounded-xl text-xs font-semibold shrink-0 cursor-pointer min-h-[44px]"
                style={{
                  color: spec.primary,
                  border: `1px solid ${spec.primary}66`,
                }}
              >
                <Palette size={17} />
                <span>Выбрать</span>
              </button>
            </div>

            {/* Data management */}
            <div
              className="rounded-3xl p-5 space-y-2.5 backdrop-blur-md"
              style={{
                backgroundColor: spec.surfaceGlass,
                border: `1px solid ${spec.cardBorder}`,
                boxShadow: spec.cardGlow,
              }}
            >
              <h2 className="font-bold text-base mb-1" style={{ color: spec.textPrimary }}>
                Управление данными
              </h2>
              <button
                type="button"
                onClick={onExportCsv}
                className="w-full flex items-center justify-center gap-2 py-3 rounded-xl text-sm font-semibold cursor-pointer min-h-[44px]"
                style={{
                  color: spec.textPrimary,
                  border: `1px solid ${spec.cardBorder}`,
                }}
              >
                <Download size={17} />
                <span>Экспортировать историю в CSV</span>
              </button>

              <button
                type="button"
                onClick={() => setShowClearDialog(true)}
                className="w-full flex items-center justify-center gap-2 py-3 rounded-xl text-sm font-semibold text-red-500 cursor-pointer min-h-[44px]"
                style={{
                  border: '1px solid rgba(239,68,68,0.35)',
                }}
              >
                <Trash2 size={17} />
                <span>Очистить всю базу</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Confirm clear modal */}
      {showClearDialog && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div
            className="w-full max-w-sm rounded-3xl p-6 space-y-4"
            style={{
              backgroundColor: spec.surface,
              border: `1px solid ${spec.cardBorder}`,
            }}
          >
            <h3 className="text-lg font-bold" style={{ color: spec.textPrimary }}>
              Очистить всю базу?
            </h3>
            <p className="text-sm" style={{ color: spec.textSecondary }}>
              Все сохранённые доходы и расходы будут удалены без возможности восстановления.
            </p>
            <div className="flex items-center justify-end gap-2">
              <button
                type="button"
                onClick={() => setShowClearDialog(false)}
                className="px-4 py-2 rounded-xl text-sm font-semibold cursor-pointer min-h-[40px]"
                style={{ color: spec.textSecondary }}
              >
                Отмена
              </button>
              <button
                type="button"
                onClick={() => {
                  setShowClearDialog(false);
                  onClearAll();
                }}
                className="px-4 py-2 rounded-xl text-sm font-bold text-red-500 cursor-pointer min-h-[40px]"
              >
                Удалить
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
