import { useEffect, useMemo, useState } from 'react';
import { PieChart, Settings, Wallet } from 'lucide-react';
import {
  AiParsedTransaction,
  fileToScaledBase64,
  parseReceiptImageWithAi,
  sanitizeGeminiApiKey,
} from './ai/aiFinanceAssistant';
import { AnalyticsScreen } from './components/AnalyticsScreen';
import { AiAssistantSheet } from './components/AiAssistantSheet';
import {
  CrowIcon,
  KeshAnimeAtmosphere,
  KeshLuxuryEmblem,
} from './components/KeshAnimeArt';
import {
  AddOperationSheet,
  OcrReviewSheet,
  ThemePickerSheet,
} from './components/KeshSheets';
import { OverviewScreen } from './components/OverviewScreen';
import { SettingsScreen } from './components/SettingsScreen';
import {
  CategoryId,
  formatSom,
  OcrParsedReceipt,
  TransactionEntity,
  TransactionSource,
  TransactionType,
} from './data/models';
import {
  createTransaction,
  exportTransactionsCsv,
  KeshPrefsState,
  loadPrefs,
  loadTransactions,
  savePrefs,
  saveTransactions,
} from './data/repository';
import { getThemeById, resolveActiveScene } from './theme/keshThemes';

type TabIndex = 0 | 1 | 2;

export function App() {
  const [prefs, setPrefs] = useState<KeshPrefsState>(() => loadPrefs());
  const [transactions, setTransactions] = useState<TransactionEntity[]>(() => loadTransactions());
  const [selectedTab, setSelectedTab] = useState<TabIndex>(0);

  const [showThemePicker, setShowThemePicker] = useState(false);
  const [showAddSheet, setShowAddSheet] = useState(false);
  const [showAiSheet, setShowAiSheet] = useState(false);
  const [ocrResult, setOcrResult] = useState<OcrParsedReceipt | null>(null);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const spec = useMemo(() => getThemeById(prefs.selectedThemeId), [prefs.selectedThemeId]);

  const activeScene = useMemo(
    () =>
      resolveActiveScene(
        spec,
        prefs.selectedSceneByTheme[spec.id],
        prefs.customPhotoByTheme[spec.id]
      ),
    [spec, prefs.selectedSceneByTheme, prefs.customPhotoByTheme]
  );

  useEffect(() => {
    savePrefs(prefs);
  }, [prefs]);

  useEffect(() => {
    saveTransactions(transactions);
  }, [transactions]);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => {
      setToastMessage((prev) => (prev === msg ? null : prev));
    }, 3200);
  };

  const handlePickGalleryPhoto = async (themeId: string, file: File) => {
    try {
      const { dataUrl } = await fileToScaledBase64(file);
      setPrefs((p) => ({
        ...p,
        customPhotoByTheme: {
          ...p.customPhotoByTheme,
          [themeId]: dataUrl,
        },
      }));
      showToast('Фото из галереи установлено');
    } catch {
      showToast('Не удалось загрузить фото из галереи');
    }
  };

  const handleClearCustomPhoto = (themeId: string) => {
    setPrefs((p) => {
      const next = { ...p.customPhotoByTheme };
      delete next[themeId];
      return { ...p, customPhotoByTheme: next };
    });
    showToast('Возвращено фото темы');
  };

  const addTransaction = (params: {
    amount: number;
    type: TransactionType;
    category: CategoryId;
    title: string;
    bankName?: string;
    source?: TransactionSource;
    note?: string;
  }) => {
    if (params.amount <= 0) return;
    const entity = createTransaction(params);
    setTransactions((prev) => [entity, ...prev]);
  };

  const handleDeleteTransaction = (tx: TransactionEntity) => {
    setTransactions((prev) => prev.filter((item) => item.id !== tx.id));
  };

  const handleScanReceiptFile = async (file: File) => {
    showToast('Обработка чека...');
    try {
      const { base64, dataUrl, mimeType } = await fileToScaledBase64(file);
      const cleanKey = sanitizeGeminiApiKey(prefs.geminiApiKey);
      if (cleanKey) {
        try {
          const aiParsed = await parseReceiptImageWithAi(cleanKey, base64, mimeType);
          setOcrResult({
            amount: aiParsed.amount,
            merchant: aiParsed.title,
            bankName: aiParsed.bankName,
            category: aiParsed.category,
            type: aiParsed.type,
            rawText: aiParsed.replyComment,
            imagePreviewUrl: dataUrl,
          });
          return;
        } catch (err) {
          showToast(err instanceof Error ? err.message : 'Ошибка ИИ при чтении чека');
        }
      }
      setOcrResult({
        amount: null,
        merchant: 'Чек (введите вручную)',
        bankName: 'Чек / Скриншот',
        category: 'OTHER_EXPENSE',
        type: 'EXPENSE',
        rawText: '',
        imagePreviewUrl: dataUrl,
      });
    } catch {
      showToast('Не удалось прочитать изображение чека');
    }
  };

  const handleAiTransactionSaved = (parsed: AiParsedTransaction) => {
    addTransaction({
      amount: parsed.amount,
      type: parsed.type,
      category: parsed.category,
      title: parsed.title,
      bankName: parsed.bankName,
      source: 'AI_ASSISTANT',
    });
    setShowAiSheet(false);
    showToast(`Записано: ${parsed.title} (${formatSom(parsed.amount)})`);
  };

  const renderNavIcon = (tabId: TabIndex, active: boolean) => {
    if (spec.animeType === 'ITACHI') {
      if (tabId === 0) {
        return <KeshLuxuryEmblem spec={spec} size={22} />;
      }
      if (tabId === 1) {
        return <CrowIcon size={22} color={active ? spec.primary : spec.textSecondary} />;
      }
    }
    if (tabId === 0) return <Wallet size={21} strokeWidth={active ? 2.4 : 1.9} />;
    if (tabId === 1) return <PieChart size={21} strokeWidth={active ? 2.4 : 1.9} />;
    return <Settings size={21} strokeWidth={active ? 2.4 : 1.9} />;
  };

  return (
    <div
      className="min-h-screen w-full transition-colors duration-300"
      style={{
        backgroundColor: spec.background,
        color: spec.textPrimary,
      }}
    >
      <KeshAnimeAtmosphere
        spec={spec}
        activeBackgroundImage={activeScene?.image}
        imagePosition={activeScene?.position}
      />

      <main className="relative z-10">
        {selectedTab === 0 && (
          <OverviewScreen
            spec={spec}
            userName={prefs.userName}
            monthlyBudget={prefs.monthlyBudget}
            transactions={transactions}
            onOpenThemes={() => setShowThemePicker(true)}
            onOpenAdd={() => setShowAddSheet(true)}
            onScanReceiptFile={handleScanReceiptFile}
            onOpenAiAssistant={() => setShowAiSheet(true)}
            onOpenManualOcr={() =>
              setOcrResult({
                amount: null,
                merchant: 'Чек / Перевод',
                bankName: 'Чек / Скриншот',
                category: 'OTHER_EXPENSE',
                type: 'EXPENSE',
                rawText: '',
              })
            }
            onDeleteTransaction={handleDeleteTransaction}
          />
        )}

        {selectedTab === 1 && (
          <AnalyticsScreen
            spec={spec}
            userName={prefs.userName}
            transactions={transactions}
            onOpenThemes={() => setShowThemePicker(true)}
          />
        )}

        {selectedTab === 2 && (
          <SettingsScreen
            spec={spec}
            userName={prefs.userName}
            monthlyBudget={prefs.monthlyBudget}
            geminiApiKey={prefs.geminiApiKey}
            hasCustomPhoto={Boolean(prefs.customPhotoByTheme[spec.id])}
            onUpdateSettings={(userName, monthlyBudget, geminiApiKey) => {
              setPrefs((p) => ({ ...p, userName, monthlyBudget, geminiApiKey }));
            }}
            onOpenThemes={() => setShowThemePicker(true)}
            onPickGalleryPhoto={(file) => void handlePickGalleryPhoto(spec.id, file)}
            onResetCustomPhoto={() => handleClearCustomPhoto(spec.id)}
            onExportCsv={() => exportTransactionsCsv(transactions)}
            onClearAll={() => {
              setTransactions([]);
              showToast('База очищена');
            }}
          />
        )}
      </main>

      {/* iPhone-style Translucent Frosted Bottom Navigation Bar */}
      <nav
        aria-label="Основная навигация"
        className="fixed bottom-0 left-0 right-0 z-40 border-t backdrop-blur-2xl"
        style={{
          backgroundColor: spec.surfaceGlass,
          borderColor: spec.cardBorder,
          boxShadow: spec.cardGlow,
        }}
      >
        <div className="max-w-xl mx-auto grid grid-cols-3 h-16">
          {[
            { id: 0 as TabIndex, label: 'Обзор' },
            { id: 1 as TabIndex, label: 'Аналитика' },
            { id: 2 as TabIndex, label: 'Настройки' },
          ].map((tab) => {
            const active = selectedTab === tab.id;
            return (
              <button
                key={tab.id}
                type="button"
                onClick={() => setSelectedTab(tab.id)}
                className="flex flex-col items-center justify-center gap-1 text-[11px] font-semibold cursor-pointer transition-colors"
                style={{
                  color: active ? spec.primary : spec.textSecondary,
                }}
              >
                {renderNavIcon(tab.id, active)}
                <span>{tab.label}</span>
              </button>
            );
          })}
        </div>
      </nav>

      {/* Toast feedback */}
      {toastMessage && (
        <div
          className="fixed bottom-20 left-1/2 -translate-x-1/2 z-50 px-4 py-2.5 rounded-2xl text-xs font-semibold shadow-lg pointer-events-none backdrop-blur-xl"
          style={{
            backgroundColor: spec.surfaceGlass,
            color: spec.textPrimary,
            border: `1px solid ${spec.cardBorder}`,
          }}
        >
          {toastMessage}
        </div>
      )}

      {/* Sheets */}
      {showAiSheet && (
        <AiAssistantSheet
          spec={spec}
          apiKey={prefs.geminiApiKey}
          onDismiss={() => setShowAiSheet(false)}
          onTransactionSaved={handleAiTransactionSaved}
          onOpenManualOcr={(imageDataUrl) => {
            setShowAiSheet(false);
            setOcrResult({
              amount: null,
              merchant: 'Чек (введите вручную)',
              bankName: 'Чек / Скриншот',
              category: 'OTHER_EXPENSE',
              type: 'EXPENSE',
              rawText: '',
              imagePreviewUrl: imageDataUrl,
            });
          }}
        />
      )}

      {showAddSheet && (
        <AddOperationSheet
          spec={spec}
          onDismiss={() => setShowAddSheet(false)}
          onSave={(amount, type, category, title, bankName) => {
            addTransaction({ amount, type, category, title, bankName, source: 'MANUAL' });
            setShowAddSheet(false);
          }}
        />
      )}

      {ocrResult && (
        <OcrReviewSheet
          spec={spec}
          parsed={ocrResult}
          onDismiss={() => setOcrResult(null)}
          onConfirm={(amount, type, category, title, bankName) => {
            addTransaction({
              amount,
              type,
              category,
              title,
              bankName,
              source: 'OCR_RECEIPT',
            });
            setOcrResult(null);
          }}
        />
      )}

      {showThemePicker && (
        <ThemePickerSheet
          currentThemeId={prefs.selectedThemeId}
          selectedSceneByTheme={prefs.selectedSceneByTheme}
          customPhotoByTheme={prefs.customPhotoByTheme}
          spec={spec}
          onSelectTheme={(selectedThemeId) => setPrefs((p) => ({ ...p, selectedThemeId }))}
          onSelectScene={(themeId, sceneId) => {
            setPrefs((p) => {
              const nextCustom = { ...p.customPhotoByTheme };
              delete nextCustom[themeId];
              return {
                ...p,
                customPhotoByTheme: nextCustom,
                selectedSceneByTheme: {
                  ...p.selectedSceneByTheme,
                  [themeId]: sceneId,
                },
              };
            });
          }}
          onPickGalleryPhoto={(themeId, file) => void handlePickGalleryPhoto(themeId, file)}
          onClearCustomPhoto={handleClearCustomPhoto}
          onDismiss={() => setShowThemePicker(false)}
        />
      )}
    </div>
  );
}

export default App;
