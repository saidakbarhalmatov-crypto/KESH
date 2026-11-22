import React, { useRef, useState } from 'react';
import { Check, ImagePlus, Loader2, Mic, Send, X } from 'lucide-react';
import {
  AiParsedTransaction,
  fileToScaledBase64,
  parseReceiptImageWithAi,
  parseTextCommandWithAi,
} from '../ai/aiFinanceAssistant';
import { CATEGORIES, detectCategoryFromText, formatSom } from '../data/models';
import { parseVoiceInput } from '../data/repository';
import { KeshThemeSpec } from '../theme/keshThemes';
import { ThemeEmblemBadge } from './KeshAnimeArt';

interface SpeechRecognitionAlternative {
  transcript: string;
}

interface SpeechRecognitionResult {
  0: SpeechRecognitionAlternative;
}

interface SpeechRecognitionEventLike {
  results: {
    0: SpeechRecognitionResult;
  };
}

interface SpeechRecognitionInstance {
  lang: string;
  interimResults: boolean;
  maxAlternatives: number;
  onresult: ((e: SpeechRecognitionEventLike) => void) | null;
  onerror: (() => void) | null;
  onend: (() => void) | null;
  start: () => void;
}

type SpeechRecognitionCtor = new () => SpeechRecognitionInstance;

export const AiAssistantSheet: React.FC<{
  spec: KeshThemeSpec;
  apiKey: string;
  onDismiss: () => void;
  onTransactionSaved: (parsed: AiParsedTransaction) => void;
}> = ({ spec, apiKey, onDismiss, onTransactionSaved }) => {
  const [inputPrompt, setInputPrompt] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isListening, setIsListening] = useState(false);
  const [statusMessage, setStatusMessage] = useState<string | null>(null);
  const [previewResult, setPreviewResult] = useState<AiParsedTransaction | null>(null);
  const imageInputRef = useRef<HTMLInputElement | null>(null);

  const accentTextColor = spec.isLight ? '#FFFFFF' : '#090C14';

  const analyzeText = async (query: string) => {
    const trimmed = query.trim();
    if (!trimmed) return;
    setIsLoading(true);
    setStatusMessage(null);
    setPreviewResult(null);

    if (apiKey.trim()) {
      try {
        const parsed = await parseTextCommandWithAi(apiKey, trimmed);
        setIsLoading(false);
        setPreviewResult(parsed);
        setStatusMessage(parsed.replyComment);
        return;
      } catch (err) {
        const fallback = parseVoiceInput(trimmed);
        setIsLoading(false);
        if (fallback) {
          const cat = detectCategoryFromText(fallback.title.toLowerCase(), fallback.type);
          setPreviewResult({
            amount: fallback.amount,
            type: fallback.type,
            category: cat,
            title: fallback.title,
            bankName: 'Локальный разбор',
            replyComment: `Разобрано локально (${err instanceof Error ? err.message : 'ошибка API'})`,
          });
          setStatusMessage('Разобрано локально: проверьте и нажмите «Подтвердить»');
        } else {
          setStatusMessage(err instanceof Error ? err.message : 'Ошибка ИИ');
        }
        return;
      }
    }

    const fallback = parseVoiceInput(trimmed);
    setIsLoading(false);
    if (fallback) {
      const cat = detectCategoryFromText(fallback.title.toLowerCase(), fallback.type);
      setPreviewResult({
        amount: fallback.amount,
        type: fallback.type,
        category: cat,
        title: fallback.title,
        bankName: 'Без API-ключа',
        replyComment: 'Для ИИ-анализа фото и сложных фраз добавьте бесплатный Gemini ключ в Настройках',
      });
      setStatusMessage('Распознано локально. Для полного ИИ добавьте бесплатный ключ в Настройках.');
    } else {
      setStatusMessage(
        'Добавьте бесплатный Gemini API ключ в Настройках, чтобы ИИ понимал любые фразы и фото.'
      );
    }
  };

  const handleVoiceInput = () => {
    const win = window as unknown as {
      SpeechRecognition?: SpeechRecognitionCtor;
      webkitSpeechRecognition?: SpeechRecognitionCtor;
    };
    const Recognition = win.SpeechRecognition || win.webkitSpeechRecognition;
    if (!Recognition) {
      setStatusMessage('Голосовой ввод недоступен в этом браузере');
      return;
    }

    try {
      const recognition = new Recognition();
      recognition.lang = 'ru-RU';
      recognition.interimResults = false;
      recognition.maxAlternatives = 1;
      setIsListening(true);
      setStatusMessage('Слушаю... Скажите доход или расход.');

      recognition.onresult = (event) => {
        const spoken = event.results?.[0]?.[0]?.transcript ?? '';
        setIsListening(false);
        if (spoken.trim()) {
          setInputPrompt(spoken);
          void analyzeText(spoken);
        }
      };
      recognition.onerror = () => {
        setIsListening(false);
        setStatusMessage('Не удалось распознать речь');
      };
      recognition.onend = () => {
        setIsListening(false);
      };
      recognition.start();
    } catch {
      setIsListening(false);
      setStatusMessage('Голосовой ввод недоступен');
    }
  };

  const handleImagePicked = async (file: File) => {
    setIsLoading(true);
    setStatusMessage('ИИ изучает фото чека...');
    setPreviewResult(null);
    try {
      const { base64, mimeType } = await fileToScaledBase64(file);
      const res = await parseReceiptImageWithAi(apiKey, base64, mimeType);
      setPreviewResult(res);
      setStatusMessage(res.replyComment);
    } catch (err) {
      setStatusMessage(err instanceof Error ? err.message : 'Не удалось распознать фото');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div
      className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4"
      onClick={onDismiss}
    >
      <div
        className="w-full sm:max-w-lg rounded-t-3xl sm:rounded-3xl p-5 max-h-[90vh] overflow-y-auto space-y-3.5"
        style={{
          backgroundColor: spec.surface,
          border: `1px solid ${spec.cardBorder}`,
          boxShadow: spec.cardGlow,
        }}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between gap-3">
          <div className="flex items-center gap-3">
            <ThemeEmblemBadge spec={spec} size={40} />
            <div>
              <h2 className="text-lg font-extrabold" style={{ color: spec.textPrimary }}>
                ИИ-Помощник KESH
              </h2>
              <p className="text-[11px]" style={{ color: spec.textSecondary }}>
                Голос, текст или фото чека · Авто-определение дохода и расхода
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

        <input
          ref={imageInputRef}
          type="file"
          accept="image/*"
          className="hidden"
          onChange={(e) => {
            const file = e.target.files?.[0];
            if (file) {
              void handleImagePicked(file);
            }
            e.target.value = '';
          }}
        />

        <form
          onSubmit={(e) => {
            e.preventDefault();
            void analyzeText(inputPrompt);
          }}
          className="flex items-center gap-2"
        >
          <input
            type="text"
            value={inputPrompt}
            onChange={(e) => setInputPrompt(e.target.value)}
            placeholder="Например: родители скинули 800 сом или купил колу за 87"
            className="flex-1 rounded-2xl px-3.5 py-3 text-sm outline-none"
            style={{
              backgroundColor: spec.background,
              color: spec.textPrimary,
              border: `1px solid ${spec.cardBorder}`,
            }}
          />
          <button
            type="button"
            onClick={handleVoiceInput}
            aria-label="Голосовой ввод"
            className="p-3 rounded-2xl cursor-pointer min-h-[44px] min-w-[44px] flex items-center justify-center"
            style={{
              backgroundColor: isListening ? `${spec.primary}33` : spec.background,
              color: spec.primary,
              border: `1px solid ${spec.cardBorder}`,
            }}
          >
            <Mic size={19} />
          </button>
          <button
            type="submit"
            aria-label="Отправить"
            className="p-3 rounded-2xl cursor-pointer min-h-[44px] min-w-[44px] flex items-center justify-center"
            style={{
              backgroundColor: spec.primary,
              color: accentTextColor,
            }}
          >
            <Send size={19} />
          </button>
        </form>

        <button
          type="button"
          onClick={() => imageInputRef.current?.click()}
          className="w-full flex items-center justify-center gap-2 py-3 rounded-2xl text-xs sm:text-sm font-semibold cursor-pointer min-h-[44px]"
          style={{
            color: spec.textPrimary,
            border: `1px solid ${spec.cardBorder}`,
          }}
        >
          <ImagePlus size={18} style={{ color: spec.primary }} />
          <span>Сканировать фото чека или скриншот через ИИ</span>
        </button>

        {isLoading && (
          <div className="flex items-center justify-center gap-2.5 py-2">
            <Loader2 size={20} className="animate-spin" style={{ color: spec.primary }} />
            <span className="text-xs" style={{ color: spec.textSecondary }}>
              ИИ анализирует...
            </span>
          </div>
        )}

        {statusMessage && (
          <p className="text-xs" style={{ color: spec.textSecondary }}>
            {statusMessage}
          </p>
        )}

        {previewResult && (
          <div
            className="rounded-2xl p-4 space-y-2.5"
            style={{
              backgroundColor: spec.surfaceVariant,
              border: `1px solid ${
                previewResult.type === 'INCOME' ? 'rgba(16,185,129,0.5)' : 'rgba(239,68,68,0.5)'
              }`,
            }}
          >
            <div className="flex items-center justify-between gap-2">
              <span
                className="text-[11px] font-bold"
                style={{
                  color: previewResult.type === 'INCOME' ? '#10B981' : '#EF4444',
                }}
              >
                {previewResult.type === 'INCOME'
                  ? 'ПРИБЫЛЬ / ДОХОД'
                  : 'РАСХОД (ВЫЧТЕТСЯ ИЗ БАЛАНСА)'}
              </span>
              <span
                className="text-lg font-extrabold tabular-nums"
                style={{
                  color: previewResult.type === 'INCOME' ? '#10B981' : '#EF4444',
                }}
              >
                {previewResult.type === 'INCOME' ? '+' : '-'}
                {formatSom(previewResult.amount)}
              </span>
            </div>

            <div className="text-sm font-semibold" style={{ color: spec.textPrimary }}>
              {previewResult.title} · {CATEGORIES[previewResult.category]?.title} ({previewResult.bankName})
            </div>

            <button
              type="button"
              onClick={() => onTransactionSaved(previewResult)}
              className="w-full flex items-center justify-center gap-2 py-3 rounded-xl font-bold text-sm cursor-pointer min-h-[44px]"
              style={{
                backgroundColor: spec.primary,
                color: accentTextColor,
              }}
            >
              <Check size={18} />
              <span>Подтвердить и записать в KESH</span>
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
