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

export interface ThemeScene {
  id: string;
  label: string;
  image: string;
  position: string;
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
  background: string;
  surface: string;
  surfaceGlass: string;
  buttonGlass: string;
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
        position: 'center 35%',
      },
      {
        id: 'itachi_anbu_moon',
        label: 'Ночь АНБУ',
        image: '/assets/aistudio/IMG_679ec574-5ded-420b-b1ca-672aaa6ab3c2_1cc1e720.jpg',
        position: 'center 42%',
      },
      {
        id: 'itachi_smile',
        label: 'Спокойствие',
        image: '/assets/aistudio/IMG_a240894e-60f9-4125-9c05-226853e251e2_4617de8e.jpg',
        position: 'center 25%',
      },
      {
        id: 'itachi_crimson_moon',
        label: 'Красная луна',
        image: itachiThroneBg,
        position: 'center 38%',
      },
    ],
    background: '#090506',
    surface: '#160B0D',
    surfaceGlass: 'rgba(18, 9, 11, 0.42)',
    buttonGlass: 'rgba(239, 35, 60, 0.56)',
    surfaceVariant: 'rgba(46, 16, 22, 0.58)',
    primary: '#EF233C',
    secondary: '#D4A373',
    cardBorder: 'rgba(255, 255, 255, 0.22)',
    cardGlow: '0 8px 32px rgba(0, 0, 0, 0.28), inset 0 1px 0 rgba(255, 255, 255, 0.2)',
    textPrimary: '#FFFFFF',
    textSecondary: '#E7D5D8',
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
        position: 'center 35%',
      },
      {
        id: 'madara_moon',
        label: 'Полная луна',
        image: madaraMoonScene,
        position: 'center 40%',
      },
    ],
    background: '#0C0504',
    surface: '#1A0B08',
    surfaceGlass: 'rgba(24, 10, 7, 0.42)',
    buttonGlass: 'rgba(255, 87, 34, 0.56)',
    surfaceVariant: 'rgba(54, 18, 12, 0.58)',
    primary: '#FF5722',
    secondary: '#FB923C',
    cardBorder: 'rgba(255, 255, 255, 0.22)',
    cardGlow: '0 8px 32px rgba(0, 0, 0, 0.28), inset 0 1px 0 rgba(255, 255, 255, 0.2)',
    textPrimary: '#FFFFFF',
    textSecondary: '#FDE2C8',
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
        position: 'center 35%',
      },
      {
        id: 'gojo_rooftop',
        label: 'Крыша Токио',
        image: gojoBlindfoldScene,
        position: 'center 30%',
      },
      {
        id: 'gojo_purple',
        label: 'Шесть Глаз',
        image: gojoPurpleScene,
        position: 'center 35%',
      },
    ],
    background: '#050C1A',
    surface: '#0B172E',
    surfaceGlass: 'rgba(10, 22, 44, 0.40)',
    buttonGlass: 'rgba(56, 189, 248, 0.52)',
    surfaceVariant: 'rgba(20, 42, 76, 0.56)',
    primary: '#38BDF8',
    secondary: '#818CF8',
    cardBorder: 'rgba(255, 255, 255, 0.24)',
    cardGlow: '0 8px 32px rgba(0, 0, 0, 0.25), inset 0 1px 0 rgba(255, 255, 255, 0.22)',
    textPrimary: '#FFFFFF',
    textSecondary: '#D6EEFF',
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
        position: 'center 35%',
      },
      {
        id: 'yuta_night',
        label: 'Ночной Токио',
        image: yutaRingScene,
        position: 'center 35%',
      },
    ],
    background: '#090514',
    surface: '#150C29',
    surfaceGlass: 'rgba(19, 10, 38, 0.42)',
    buttonGlass: 'rgba(168, 85, 247, 0.54)',
    surfaceVariant: 'rgba(42, 22, 76, 0.56)',
    primary: '#A855F7',
    secondary: '#818CF8',
    cardBorder: 'rgba(255, 255, 255, 0.22)',
    cardGlow: '0 8px 32px rgba(0, 0, 0, 0.28), inset 0 1px 0 rgba(255, 255, 255, 0.2)',
    textPrimary: '#FFFFFF',
    textSecondary: '#EBD9FF',
  },
  {
    id: 'mikasa',
    name: 'Mikasa',
    characterTitle: 'Микаса Аккерман · Алый шарф',
    motto: 'Крылья Свободы · Сталь и рассвет',
    subtitle: 'Холодное небо, алый шарф и клинки Разведкорпуса',
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
        position: 'center 28%',
      },
      {
        id: 'mikasa_dawn',
        label: 'Рассвет у стен',
        image: mikasaDawnScene,
        position: 'center 35%',
      },
      {
        id: 'mikasa_belltower',
        label: 'Над городом',
        image: mikasaScarfBg,
        position: 'center 35%',
      },
    ],
    background: '#11161F',
    surface: '#1A212E',
    surfaceGlass: 'rgba(20, 27, 38, 0.40)',
    buttonGlass: 'rgba(225, 29, 72, 0.56)',
    surfaceVariant: 'rgba(42, 52, 68, 0.56)',
    primary: '#F43F5E',
    secondary: '#CBD5E1',
    cardBorder: 'rgba(255, 255, 255, 0.24)',
    cardGlow: '0 8px 32px rgba(0, 0, 0, 0.24), inset 0 1px 0 rgba(255, 255, 255, 0.22)',
    textPrimary: '#FFFFFF',
    textSecondary: '#E2E8F0',
  },
  {
    id: 'marin',
    name: 'Marin',
    characterTitle: 'Марин Китагава · Вечерний закат',
    motto: 'Закатное небо · Тёплые огни улицы',
    subtitle: 'Сумерки мегаполиса, огни фонарей и нежный закат',
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
        position: '38% 85%',
      },
      {
        id: 'marin_cozy',
        label: 'Тёплый вечер',
        image: marinCozyScene,
        position: 'center 30%',
      },
      {
        id: 'marin_sunset',
        label: 'Закат Токио',
        image: marinSunsetBg,
        position: 'center 35%',
      },
    ],
    background: '#161B26',
    surface: '#212736',
    surfaceGlass: 'rgba(22, 27, 40, 0.36)',
    buttonGlass: 'rgba(244, 140, 127, 0.52)',
    surfaceVariant: 'rgba(48, 54, 74, 0.52)',
    primary: '#FDA4AF',
    secondary: '#FBBF24',
    cardBorder: 'rgba(255, 255, 255, 0.24)',
    cardGlow: '0 8px 32px rgba(0, 0, 0, 0.24), inset 0 1px 0 rgba(255, 255, 255, 0.24)',
    textPrimary: '#FFFFFF',
    textSecondary: '#F1F5F9',
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
    background: '#090D16',
    surface: '#111827',
    surfaceGlass: 'rgba(17, 24, 39, 0.72)',
    buttonGlass: 'rgba(16, 185, 129, 0.82)',
    surfaceVariant: 'rgba(31, 41, 55, 0.85)',
    primary: '#10B981',
    secondary: '#3B82F6',
    cardBorder: 'rgba(255, 255, 255, 0.14)',
    cardGlow: '0 8px 24px rgba(0, 0, 0, 0.25)',
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
    background: '#F1F5F9',
    surface: '#FFFFFF',
    surfaceGlass: 'rgba(255, 255, 255, 0.72)',
    buttonGlass: 'rgba(37, 99, 235, 0.88)',
    surfaceVariant: 'rgba(226, 232, 240, 0.85)',
    primary: '#2563EB',
    secondary: '#0EA5E9',
    cardBorder: 'rgba(15, 23, 42, 0.10)',
    cardGlow: '0 6px 20px rgba(15, 23, 42, 0.06)',
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
  selectedSceneId?: string,
  customPhotoUrl?: string
): ThemeScene | null {
  if (customPhotoUrl) {
    return {
      id: 'custom_gallery_photo',
      label: 'Фото из галереи',
      image: customPhotoUrl,
      position: 'center center',
    };
  }

  if (!spec.isAnime || spec.scenes.length === 0) return null;

  if (selectedSceneId) {
    const found = spec.scenes.find((s) => s.id === selectedSceneId);
    if (found) return found;
  }

  return spec.scenes[0];
}
