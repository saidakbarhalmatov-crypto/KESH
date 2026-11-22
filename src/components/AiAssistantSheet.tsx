import React, { useEffect, useRef, useState } from 'react';
import { Camera, Check, ImagePlus, Loader2, Mic, Send, Square, X } from 'lucide-react';
import {
  AiParsedTransaction,
  blobToBase64,
  fileToScaledBase64,
  parseAudioCommandWithAi,
  parseReceiptImageWithAi,
  parseTextCommandWithAi,
  sanitizeGeminiApiKey,
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
  stop?: () => void;
}

type SpeechRecognitionCtor = new () => SpeechRecognitionInstance;

export const AiAssistantSheet: React.FC<{
  spec: KeshThemeSpec;
  apiKey: string;
  onDismiss: () => void;
  onTransactionSaved: (parsed: AiParsedTransaction) => void;
  onOpenManualOcr?: (imageDataUrl: string) => void;
}> = ({ spec, apiKey, onDismiss, onTransactionSaved, onOpenManualOcr }) => {
  const [inputPrompt, setInputPrompt] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isListening, setIsListening] = useState(false);
  const [statusMessage, setStatusMessage] = useState<string | null>(null);
  const [previewResult, setPreviewResult] = useState<AiParsedTransaction | null>(null);
  const [lastFailedImageUrl, setLastFailedImageUrl] = useState<string | null>(null);

  const galleryInputRef = useRef<HTMLInputElement | null>(null);
  const cameraInputRef = useRef<HTMLInputElement | null>(null);
  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const mediaStreamRef = useRef<MediaStream | null>(null);
  const audioChunksRef = useRef<Blob[]>([]);

  const accentTextColor = spec.isLight ? '#FFFFFF' : '#090C14';
  const cleanKey = sanitizeGeminiApiKey(apiKey);

  const stopActiveRecordingResources = () => {
    try {
      if (mediaRecorderRef.current && mediaRecorderRef.current.state !== 'inactive') {
        mediaRecorderRef.current.stop();
      }
    } catch {
      // ignore
    }
    if (mediaStreamRef.current) {
      mediaStreamRef.current.getTracks().forEach((t) => t.stop());
      mediaStreamRef.current = null;
    }
    mediaRecorderRef.current = null;
  };

  useEffect(() => {
    return () => {
      if (mediaStreamRef.current) {
        mediaStreamRef.current.getTracks().forEach((t) => t.stop());
      }
    };
  }, []);

  const analyzeText = async (query: string) => {
    const trimmed = query.trim();
    if (!trimmed) return;
    setIsLoading(true);
    setStatusMessage(null);
    setPreviewResult(null);
    setLastFailedImageUrl(null);

    if (cleanKey) {
      try {
        const parsed = await parseTextCommandWithAi(cleanKey, trimmed);
        setIsLoading(false);
        setPreviewResult(parsed);
        setStatusMessage(parsed.replyComment);
        return;
      } catch (err) {
        const fallback = parseVoiceInput(trimmed);
        setIsLoading(false);
        const errMsg = err instanceof Error ? err.message : 'ошибка API';
        if (fallback) {
          const cat = detectCategoryFromText(fallback.title.toLowerCase(), fallback.type);
          setPreviewResult({
            amount: fallback.amount,
            type: fallback.type,
            category: cat,
            title: fallback.title,
            bankName: 'Локальный разбор',
            replyComment: `Разобрано локально (${errMsg})`,
          });
          setStatusMessage(`${errMsg} — разобрано локально, проверьте и подтвердите.`);
        } else {
          setStatusMessage(errMsg);
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
        replyComment: 'Для ИИ-анализа фото и голоса добавьте бесплатный Gemini ключ в Настройках',
      });
      setStatusMessage('Распознано локально. Для полного ИИ добавьте бесплатный ключ в Настройках.');
    } else {
      setStatusMessage(
        'Добавьте бесплатный Gemini API ключ в Настройках, чтобы ИИ понимал любые фразы, голос и фото.'
      );
    }
  };

  const startMediaRecorder = async () => {
    if (
      typeof navigator.mediaDevices === 'undefined' ||
      typeof navigator.mediaDevices.getUserMedia !== 'function' ||
      typeof MediaRecorder === 'undefined'
    ) {
      setStatusMessage('Запись звука недоступна на этом устройстве');
      return;
    }

    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      mediaStreamRef.current = stream;

      const preferredTypes = [
        'audio/webm;codecs=opus',
        'audio/webm',
        'audio/mp4',
        'audio/ogg;codecs=opus',
      ];
      const supportedType = preferredTypes.find((t) => MediaRecorder.isTypeSupported?.(t));
      const recorder = supportedType
        ? new MediaRecorder(stream, { mimeType: supportedType })
        : new MediaRecorder(stream);

      audioChunksRef.current = [];
      mediaRecorderRef.current = recorder;

      recorder.ondataavailable = (ev) => {
        if (ev.data && ev.data.size > 0) {
          audioChunksRef.current.push(ev.data);
        }
      };

      recorder.onstop = async () => {
        const mimeType = recorder.mimeType || 'audio/webm';
        const blob = new Blob(audioChunksRef.current, { type: mimeType });
        stream.getTracks().forEach((t) => t.stop());
        mediaStreamRef.current = null;
        mediaRecorderRef.current = null;
        setIsListening(false);

        if (blob.size < 500) {
          setStatusMessage('Запись слишком короткая. Попробуйте сказать фразу чуть дольше.');
          return;
        }

        if (!cleanKey) {
          setStatusMessage('Запись получена, но для голосового ИИ нужен Gemini API ключ в Настройках.');
          return;
        }

        setIsLoading(true);
        setStatusMessage('ИИ слушает вашу голосовую команду...');
        setPreviewResult(null);
        try {
          const { base64, mimeType: resolvedMime } = await blobToBase64(blob);
          const parsed = await parseAudioCommandWithAi(cleanKey, base64, resolvedMime);
          setPreviewResult(parsed);
          setStatusMessage(parsed.replyComment);
        } catch (err) {
          setStatusMessage(err instanceof Error ? err.message : 'Не удалось распознать голос');
        } finally {
          setIsLoading(false);
        }
      };

      recorder.start();
      setIsListening(true);
      setStatusMessage('Идёт запись... Скажите сумму и операцию, затем нажмите красную кнопку ещё раз.');
    } catch {
      setIsListening(false);
      setStatusMessage('Нет доступа к микрофону. Разрешите микрофон для KESH в настройках телефона.');
    }
  };

  const handleVoiceInput = () => {
    if (isListening) {
      stopActiveRecordingResources();
      return;
    }

    const win = window as unknown as {
      SpeechRecognition?: SpeechRecognitionCtor;
      webkitSpeechRecognition?: SpeechRecognitionCtor;
    };
    const Recognition = win.SpeechRecognition || win.webkitSpeechRecognition;

    // On Android WebView (Capacitor), SpeechRecognition is absent -> use MediaRecorder + Gemini audio.
    // Also prefer MediaRecorder when Gemini API key is configured for consistent multilingual accuracy.
    const canRecordAudio =
      typeof navigator.mediaDevices !== 'undefined' &&
      typeof navigator.mediaDevices.getUserMedia === 'function' &&
      typeof MediaRecorder !== 'undefined';

    if ((cleanKey && canRecordAudio) || !Recognition) {
      void startMediaRecorder();
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
        void startMediaRecorder();
      };
      recognition.onend = () => {
        setIsListening(false);
      };
      recognition.start();
    } catch {
      setIsListening(false);
      void startMediaRecorder();
    }
  };

  const handleImagePicked = async (file: File) => {
    setIsLoading(true);
    setStatusMessage('ИИ изучает фото чека...');
    setPreviewResult(null);
    setLastFailedImageUrl(null);
    try {
      const { base64, dataUrl, mimeType } = await fileToScaledBase64(file);
      try {
        const res = await parseReceiptImageWithAi(cleanKey, base64, mimeType);
        setPreviewResult(res);
        setStatusMessage(res.replyComment);
      } catch (err) {
        setLastFailedImageUrl(dataUrl);
        setStatusMessage(err instanceof Error ? err.message : 'Не удалось распознать фото');
      }
    } catch (err) {
      setStatusMessage(err instanceof Error ? err.message : 'Не удалось прочитать файл изображения');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div
      className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4"
      onClick={() => {
        stopActiveRecordingResources();
        onDismiss();
      }}
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
            onClick={() => {
              stopActiveRecordingResources();
              onDismiss();
            }}
            aria-label="Закрыть"
            className="p-2 rounded-xl cursor-pointer min-h-[40px] min-w-[40px] flex items-center justify-center"
            style={{ color: spec.textSecondary }}
          >
            <X size={20} />
          </button>
        </div>

        <input
          ref={galleryInputRef}
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

        <input
          ref={cameraInputRef}
          type="file"
          accept="image/*"
          capture="environment"
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
            aria-label={isListening ? 'Остановить запись' : 'Голосовой ввод'}
            className={`p-3 rounded-2xl cursor-pointer min-h-[44px] min-w-[44px] flex items-center justify-center transition-colors ${
              isListening ? 'animate-pulse' : ''
            }`}
            style={{
              backgroundColor: isListening ? 'rgba(239, 68, 68, 0.25)' : spec.background,
              color: isListening ? '#EF4444' : spec.primary,
              border: `1px solid ${isListening ? '#EF4444' : spec.cardBorder}`,
            }}
          >
            {isListening ? <Square size={18} fill="#EF4444" /> : <Mic size={19} />}
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

        <div className="grid grid-cols-2 gap-2">
          <button
            type="button"
            onClick={() => cameraInputRef.current?.click()}
            className="flex items-center justify-center gap-2 py-3 px-2.5 rounded-2xl text-xs font-semibold cursor-pointer min-h-[44px]"
            style={{
              color: spec.textPrimary,
              border: `1px solid ${spec.cardBorder}`,
            }}
          >
            <Camera size={17} style={{ color: spec.primary }} />
            <span>Камера (чек)</span>
          </button>
          <button
            type="button"
            onClick={() => galleryInputRef.current?.click()}
            className="flex items-center justify-center gap-2 py-3 px-2.5 rounded-2xl text-xs font-semibold cursor-pointer min-h-[44px]"
            style={{
              color: spec.textPrimary,
              border: `1px solid ${spec.cardBorder}`,
            }}
          >
            <ImagePlus size={17} style={{ color: spec.primary }} />
            <span>Галерея / Скрин</span>
          </button>
        </div>

        {isLoading && (
          <div className="flex items-center justify-center gap-2.5 py-2">
            <Loader2 size={20} className="animate-spin" style={{ color: spec.primary }} />
            <span className="text-xs" style={{ color: spec.textSecondary }}>
              ИИ анализирует...
            </span>
          </div>
        )}

        {statusMessage && (
          <p className="text-xs leading-relaxed" style={{ color: spec.textSecondary }}>
            {statusMessage}
          </p>
        )}

        {lastFailedImageUrl && onOpenManualOcr && (
          <button
            type="button"
            onClick={() => onOpenManualOcr(lastFailedImageUrl)}
            className="w-full py-2.5 px-3 rounded-xl text-xs font-bold cursor-pointer"
            style={{
              backgroundColor: `${spec.primary}26`,
              color: spec.primary,
              border: `1px solid ${spec.cardBorder}`,
            }}
          >
            Открыть фото в ручном редакторе чеков
          </button>
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
