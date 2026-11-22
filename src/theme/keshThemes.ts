import itachiThroneBg from '../assets/images/itachi_throne_bg_1795351141953.jpg';
import madaraFlamesBg from '../assets/images/madara_flames_bg_1795351142268.jpg';
import madaraMoonScene from '../assets/images/madara_moon_scene_1795353298220.jpg';
import gojoInfinityBg from '../assets/images/gojo_infinity_bg_1795351143083.jpg';
import gojoBlindfoldScene from '../assets/images/gojo_blindfold_scene_1795353297600.jpg';
import gojoPurpleScene from '../assets/images/gojo_purple_scene_1795353298476.jpg';
import yutaCursedBg from '../assets/images/yuta_cursed_bg_1795351142009.jpg';
import yutaRingScene from '../assets/images/yuta_ring_scene_1795353298805.jpg';
import mikasaScarfBg from '../assets/images/mikasa_scarf_bg_1795351141980.jpg';
import mikasaDawnScene from '../assets/images/mikasa_dawn_scene_1795353298797.jpg';
import marinSunsetBg from '../assets/images/marin_sunset_bg_1795351142197.jpg';
import marinCozyScene from '../assets/images/marin_cozy_scene_1795353298699.jpg';

export type AnimeVisualType =
  | 'NONE'
  | 'ITACHI'
  | 'MADARA'
  | 'GOJO'
  | 'YUTA'
  | 'MIKASA'
  | 'MARIN';

export type SceneMood = 'calm' | 'focus' | 'alert';

export interface ThemeScene {
  id: string;
  label: string;
  image: string;
  mood: SceneMood;
}

export interface KeshThemeSpec {
  id: string;
  name: string;
  characterTitle: string;
  motto: string;
  subtitle: string;
  receiptTitle: string;
  analyticsLabel: string;
  symbolTag: string;
  animeType: AnimeVisualType;
  isAnime: boolean;
  isDark: boolean;
  isLight: boolean;
  backgroundImage?: string;
  scenes: ThemeScene[];
  quotes: {
    healthy: string;
    caution: string;
    alert: string;
  };
  background: string;
  surface: string;
  surfaceGlass: string;
  surfaceVariant: string;
  primary: string;
  secondary: string;
  cardBorder: string;
  cardGlow: string;
  textPrimary: string;
  textSecondary: string;
}

export const KESH_THEMES: KeshThemeSpec[] = [
  {
    id: 'itachi',
    name: 'Itachi',
    characterTitle: 'Итачи Учиха · Трон Акацуки',
    motto: 'Мангекё Шаринган · Красная луна и вороны',
    subtitle: 'Трон Учихи, ночь АНБУ, алая луна и облака Акацуки',
    receiptTitle: 'Свиток чеков Акацуки',
    analyticsLabel: 'Хроника Шарингана',
    symbolTag: 'SHARINGAN',
    animeType: 'ITACHI',
    isAnime: true,
    isDark: true,
    isLight: false,
    backgroundImage: '/assets/aistudio/4f93cd372dc13a218b8edc9a86a13539_ee7537d8.jpg',
    scenes: [
      {
        id: 'itachi_throne_photo',
        label: 'Трон Учихи',
        image: '/assets/aistudio/4f93cd372dc13a218b8edc9a86a13539_ee7537d8.jpg',
        mood: 'focus',
      },
      {
        id: 'itachi_anbu_moon',
        label: 'Ночь АНБУ',
        image: '/assets/aistudio/IMG_679ec574-5ded-420b-b1ca-672aaa6ab3c2_1cc1e720.jpg',
        mood: 'alert',
      },
      {
        id: 'itachi_smile',
        label: 'Спокойствие',
        image: '/assets/aistudio/IMG_a240894e-60f9-4125-9c05-226853e251e2_4617de8e.jpg',
        mood: 'calm',
      },
      {
        id: 'itachi_crimson_moon',
        label: 'Красная луна',
        image: itachiThroneBg,
        mood: 'focus',
      },
    ],
    quotes: {
      healthy: 'Бюджет под полным контролем. Истинная сила — в спокойствии и расчёте.',
      caution: 'Половина лимита пройдена. Наблюдай за каждой тратой взглядом Шарингана.',
      alert: 'Лимит почти исчерпан! Остановись и сохрани резерв на конец месяца.',
    },
    background: '#080304',
    surface: '#110608',
    surfaceGlass: 'rgba(12, 5, 7, 0.84)',
    surfaceVariant: '#2A0D12',
    primary: '#EF233C',
    secondary: '#9B0000',
    cardBorder: 'rgba(239, 35, 60, 0.48)',
    cardGlow: '0 0 24px rgba(239, 35, 60, 0.18)',
    textPrimary: '#F8FAFC',
    textSecondary: '#E2C4C8',
  },
  {
    id: 'madara',
    name: 'Madara',
    characterTitle: 'Мадара Учиха · Абсолютное Сусаноо',
    motto: 'Вечный Мангекё · Пламя и Сусаноо',
    subtitle: 'Вулканические искры, полная луна и багровое пламя',
    receiptTitle: 'Огненный реестр чеков',
    analyticsLabel: 'Пламя Сусаноо',
    symbolTag: 'ETERNAL',
    animeType: 'MADARA',
    isAnime: true,
    isDark: true,
    isLight: false,
    backgroundImage: madaraFlamesBg,
    scenes: [
      {
        id: 'madara_flames',
        label: 'Пламя Сусаноо',
        image: madaraFlamesBg,
        mood: 'focus',
      },
      {
        id: 'madara_moon',
        label: 'Полная луна',
        image: madaraMoonScene,
        mood: 'calm',
      },
    ],
    quotes: {
      healthy: 'Твоя казна крепка, как броня Сусаноо. Продолжай держать темп.',
      caution: 'Расходы разгораются. Направь траты в нужное русло, пока не вспыхнул лимит.',
      alert: 'Опасная черта! Лимит месяца горит — пора закрыть лишние расходы.',
    },
    background: '#0B0403',
    surface: '#170907',
    surfaceGlass: 'rgba(18, 7, 5, 0.84)',
    surfaceVariant: '#33120B',
    primary: '#FF5722',
    secondary: '#DC2626',
    cardBorder: 'rgba(255, 87, 34, 0.48)',
    cardGlow: '0 0 24px rgba(255, 87, 34, 0.18)',
    textPrimary: '#FFF7ED',
    textSecondary: '#FDBA74',
  },
  {
    id: 'gojo',
    name: 'Gojo',
    characterTitle: 'Сатору Годжо · Безграничность',
    motto: 'Безграничность · Шесть Глаз',
    subtitle: 'Космический эфир, ночной Токио и ледяной неон',
    receiptTitle: 'Сканер Бесконечности',
    analyticsLabel: 'Область Пустоты',
    symbolTag: 'INFINITY',
    animeType: 'GOJO',
    isAnime: true,
    isDark: true,
    isLight: false,
    backgroundImage: gojoInfinityBg,
    scenes: [
      {
        id: 'gojo_infinity',
        label: 'Безграничность',
        image: gojoInfinityBg,
        mood: 'focus',
      },
      {
        id: 'gojo_rooftop',
        label: 'Крыша Токио',
        image: gojoBlindfoldScene,
        mood: 'calm',
      },
      {
        id: 'gojo_purple',
        label: 'Шесть Глаз',
        image: gojoPurpleScene,
        mood: 'alert',
      },
    ],
    quotes: {
      healthy: 'Расслабься — с таким запасом бюджета ты буквально в Безграничности!',
      caution: 'Шесть Глаз видят каждую покупку. Половина лимита уже позади.',
      alert: 'Внимание: барьер лимита почти пробит. Включаем режим экономии!',
    },
    background: '#030914',
    surface: '#081529',
    surfaceGlass: 'rgba(7, 17, 36, 0.82)',
    surfaceVariant: '#112545',
    primary: '#38BDF8',
    secondary: '#818CF8',
    cardBorder: 'rgba(56, 189, 248, 0.45)',
    cardGlow: '0 0 24px rgba(56, 189, 248, 0.18)',
    textPrimary: '#F0F9FF',
    textSecondary: '#BAE6FD',
  },
  {
    id: 'yuta',
    name: 'Yuta Okkotsu',
    characterTitle: 'Юта Оккоцу · Проклятая Катана',
    motto: 'Проклятая энергия · Чистая любовь',
    subtitle: 'Ночной Токио, клинок и фиолетовая аура проклятий',
    receiptTitle: 'Архив проклятых чеков',
    analyticsLabel: 'Баланс энергии',
    symbolTag: 'CURSED',
    animeType: 'YUTA',
    isAnime: true,
    isDark: true,
    isLight: false,
    backgroundImage: yutaCursedBg,
    scenes: [
      {
        id: 'yuta_katana',
        label: 'Катана и аура',
        image: yutaCursedBg,
        mood: 'focus',
      },
      {
        id: 'yuta_night',
        label: 'Ночной Токио',
        image: yutaRingScene,
        mood: 'calm',
      },
    ],
    quotes: {
      healthy: 'Спокойный баланс — лучший щит. Все категории в стабильном плюсе.',
      caution: 'Энергия бюджета распределяется активно — проверь топ категорий в аналитике.',
      alert: 'Резерв на исходе! Самое время притормозить спонтанные покупки.',
    },
    background: '#080412',
    surface: '#120A24',
    surfaceGlass: 'rgba(15, 8, 30, 0.84)',
    surfaceVariant: '#271447',
    primary: '#A855F7',
    secondary: '#6366F1',
    cardBorder: 'rgba(168, 85, 247, 0.46)',
    cardGlow: '0 0 24px rgba(168, 85, 247, 0.18)',
    textPrimary: '#FAF5FF',
    textSecondary: '#E9D5FF',
  },
  {
    id: 'mikasa',
    name: 'Mikasa',
    characterTitle: 'Микаса Аккерман · Крылья Свободы',
    motto: 'Крылья Свободы · Алый шарф',
    subtitle: 'Алый шарф, сталь клинков и рассвет над стенами',
    receiptTitle: 'Отчёт разведкорпуса',
    analyticsLabel: 'Сводка Разведки',
    symbolTag: 'WINGS',
    animeType: 'MIKASA',
    isAnime: true,
    isDark: true,
    isLight: false,
    backgroundImage: '/assets/aistudio/0dc010f1dc874763b7769d10f1c16ca3_0e33af78.jpg',
    scenes: [
      {
        id: 'mikasa_scarf_photo',
        label: 'Алый шарф',
        image: '/assets/aistudio/0dc010f1dc874763b7769d10f1c16ca3_0e33af78.jpg',
        mood: 'calm',
      },
      {
        id: 'mikasa_dawn',
        label: 'Рассвет у стен',
        image: mikasaDawnScene,
        mood: 'focus',
      },
      {
        id: 'mikasa_belltower',
        label: 'Над городом',
        image: mikasaScarfBg,
        mood: 'alert',
      },
    ],
    quotes: {
      healthy: 'Чёткая дисциплина. Запасов достаточно, продолжай двигаться вперёд.',
      caution: 'Половина лимита израсходована — держи фокус только на важном.',
      alert: 'Лимит на критическом уровне! Защити свой остаток до конца месяца.',
    },
    background: '#0A0B0E',
    surface: '#13161C',
    surfaceGlass: 'rgba(16, 19, 25, 0.85)',
    surfaceVariant: '#252A36',
    primary: '#E11D48',
    secondary: '#94A3B8',
    cardBorder: 'rgba(225, 29, 72, 0.44)',
    cardGlow: '0 0 22px rgba(225, 29, 72, 0.15)',
    textPrimary: '#F8FAFC',
    textSecondary: '#CBD5E1',
  },
  {
    id: 'marin',
    name: 'Marin',
    characterTitle: 'Марин Китагава · Закат Токио',
    motto: 'Закат в городе · Тёплые огни',
    subtitle: 'Вечерняя улица, уютный свет и закатное небо',
    receiptTitle: 'Вечерние чеки и покупки',
    analyticsLabel: 'Закатная палитра',
    symbolTag: 'SUNSET',
    animeType: 'MARIN',
    isAnime: true,
    isDark: true,
    isLight: false,
    backgroundImage: '/assets/aistudio/698cf351990225b0ffe748080a2f7568_afd297c8.jpg',
    scenes: [
      {
        id: 'marin_street_photo',
        label: 'Вечерняя улица',
        image: '/assets/aistudio/698cf351990225b0ffe748080a2f7568_afd297c8.jpg',
        mood: 'calm',
      },
      {
        id: 'marin_cozy',
        label: 'Тёплый вечер',
        image: marinCozyScene,
        mood: 'focus',
      },
      {
        id: 'marin_sunset',
        label: 'Закат Токио',
        image: marinSunsetBg,
        mood: 'alert',
      },
    ],
    quotes: {
      healthy: 'Отличный баланс! Можно спокойно копить на крутую мечту или косплей.',
      caution: 'Шопинг — это здорово, но давай заглянем в лимит, чтобы хватило на всё!',
      alert: 'Ой, лимит почти закончился! Сделаем паузу в покупках на пару дней.',
    },
    background: '#130914',
    surface: '#201021',
    surfaceGlass: 'rgba(27, 13, 28, 0.82)',
    surfaceVariant: '#3B1D3B',
    primary: '#F472B6',
    secondary: '#FB923C',
    cardBorder: 'rgba(244, 114, 182, 0.45)',
    cardGlow: '0 0 24px rgba(244, 114, 182, 0.18)',
    textPrimary: '#FFF1F2',
    textSecondary: '#FBCFE8',
  },
  {
    id: 'dark_pro',
    name: 'Тёмный Графит',
    characterTitle: 'KESH Minimal · Тёмный Графит',
    motto: 'Строгий минимализм',
    subtitle: 'Тёмный режим без фоновых иллюстраций',
    receiptTitle: 'Распознавание чека (OCR)',
    analyticsLabel: 'Финансовая сводка',
    symbolTag: 'MINIMAL',
    animeType: 'NONE',
    isAnime: false,
    isDark: true,
    isLight: false,
    scenes: [],
    quotes: {
      healthy: 'Баланс в норме.',
      caution: 'Половина лимита пройдена.',
      alert: 'Лимит расходов почти исчерпан.',
    },
    background: '#090D16',
    surface: '#111827',
    surfaceGlass: 'rgba(17, 24, 39, 0.96)',
    surfaceVariant: '#1F2937',
    primary: '#10B981',
    secondary: '#3B82F6',
    cardBorder: 'rgba(31, 41, 55, 1)',
    cardGlow: 'none',
    textPrimary: '#F9FAFB',
    textSecondary: '#9CA3AF',
  },
  {
    id: 'light_clean',
    name: 'Светлая классика',
    characterTitle: 'KESH Clean · Дневной режим',
    motto: 'Чистый дневной режим',
    subtitle: 'Светлый фон для яркого дня',
    receiptTitle: 'Распознавание чека (OCR)',
    analyticsLabel: 'Финансовая сводка',
    symbolTag: 'LIGHT',
    animeType: 'NONE',
    isAnime: false,
    isDark: false,
    isLight: true,
    scenes: [],
    quotes: {
      healthy: 'Баланс в норме.',
      caution: 'Половина лимита пройдена.',
      alert: 'Лимит расходов почти исчерпан.',
    },
    background: '#F8FAFC',
    surface: '#FFFFFF',
    surfaceGlass: 'rgba(255, 255, 255, 0.96)',
    surfaceVariant: '#E2E8F0',
    primary: '#2563EB',
    secondary: '#0EA5E9',
    cardBorder: 'rgba(226, 232, 240, 1)',
    cardGlow: '0 4px 16px rgba(15, 23, 42, 0.05)',
    textPrimary: '#0F172A',
    textSecondary: '#475569',
  },
];

export function getThemeById(id?: string | null): KeshThemeSpec {
  if (!id) return KESH_THEMES[0];
  return KESH_THEMES.find((t) => t.id.toLowerCase() === id.toLowerCase()) ?? KESH_THEMES[0];
}

export function resolveActiveScene(
  spec: KeshThemeSpec,
  selectedSceneId: string | undefined,
  autoMoodScene: boolean,
  budgetRatio: number
): ThemeScene | null {
  if (!spec.isAnime || spec.scenes.length === 0) return null;

  if (autoMoodScene) {
    const targetMood: SceneMood =
      budgetRatio >= 0.85 ? 'alert' : budgetRatio >= 0.45 ? 'focus' : 'calm';
    const byMood = spec.scenes.find((s) => s.mood === targetMood);
    if (byMood) return byMood;
  }

  if (selectedSceneId) {
    const found = spec.scenes.find((s) => s.id === selectedSceneId);
    if (found) return found;
  }

  return spec.scenes[0];
}
