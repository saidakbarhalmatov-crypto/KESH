import React from 'react';
import {
  Utensils,
  ShoppingCart,
  Car,
  ShoppingBag,
  Zap,
  HeartPulse,
  Film,
  ArrowLeftRight,
  MoreHorizontal,
  Banknote,
  Briefcase,
  Landmark,
  Gift,
  Wallet,
  Palette,
} from 'lucide-react';
import { CATEGORIES, CategoryId } from '../data/models';
import { KeshThemeSpec } from '../theme/keshThemes';
import { CrowIcon, KeshLuxuryEmblem } from './KeshAnimeArt';

export function getCategoryIcon(categoryId: CategoryId, size = 20, color?: string) {
  const props = { size, color, strokeWidth: 2.2 };
  switch (categoryId) {
    case 'FOOD':
      return <Utensils {...props} />;
    case 'GROCERIES':
      return <ShoppingCart {...props} />;
    case 'TRANSPORT':
      return <Car {...props} />;
    case 'SHOPPING':
      return <ShoppingBag {...props} />;
    case 'BILLS':
      return <Zap {...props} />;
    case 'HEALTH':
      return <HeartPulse {...props} />;
    case 'ENTERTAINMENT':
      return <Film {...props} />;
    case 'TRANSFER':
      return <ArrowLeftRight {...props} />;
    case 'OTHER_EXPENSE':
      return <MoreHorizontal {...props} />;
    case 'SALARY':
      return <Banknote {...props} />;
    case 'FREELANCE':
      return <Briefcase {...props} />;
    case 'TRANSFER_IN':
      return <Landmark {...props} />;
    case 'CASHBACK':
      return <Gift {...props} />;
    case 'OTHER_INCOME':
      return <Wallet {...props} />;
  }
}

export const CategoryBadge: React.FC<{
  categoryId: CategoryId;
  size?: number;
}> = ({ categoryId, size = 42 }) => {
  const cat = CATEGORIES[categoryId] ?? CATEGORIES.OTHER_EXPENSE;
  return (
    <div
      className="flex items-center justify-center rounded-[13px] shrink-0"
      style={{
        width: size,
        height: size,
        backgroundColor: `${cat.colorHex}29`,
      }}
      title={cat.title}
    >
      {getCategoryIcon(categoryId, Math.round(size * 0.5), cat.colorHex)}
    </div>
  );
};

export const KeshTopHeader: React.FC<{
  userName: string;
  spec: KeshThemeSpec;
  onOpenThemes: () => void;
}> = ({ userName, spec, onOpenThemes }) => {
  return (
    <header className="flex items-center justify-between gap-4 px-4 sm:px-6 py-3.5 relative z-10">
      <div
        className="flex items-center gap-3 min-w-0 rounded-2xl px-3 py-1.5 backdrop-blur-xl"
        style={{
          backgroundColor: spec.surfaceGlass,
          border: `1px solid ${spec.cardBorder}`,
          boxShadow: spec.cardGlow,
        }}
      >
        <KeshLuxuryEmblem spec={spec} size={38} />
        <div className="min-w-0">
          <div className="flex items-center gap-2">
            <span
              className="font-extrabold text-base tracking-wider whitespace-nowrap"
              style={{ color: spec.textPrimary }}
            >
              KESH
            </span>
            <span
              className="text-[10px] font-bold px-1.5 py-0.5 rounded-md whitespace-nowrap"
              style={{
                backgroundColor: `${spec.primary}33`,
                color: spec.primary,
              }}
            >
              {spec.symbolTag}
            </span>
          </div>
          <div className="flex items-center gap-1.5 text-[11px] truncate" style={{ color: spec.textSecondary }}>
            <span className="font-medium truncate">{userName}</span>
            <span aria-hidden="true">·</span>
            <span className="truncate">{spec.characterTitle}</span>
          </div>
        </div>
      </div>

      <button
        type="button"
        onClick={onOpenThemes}
        className="flex items-center gap-2 px-3.5 py-2.5 rounded-2xl text-xs font-semibold transition-transform active:scale-95 cursor-pointer shrink-0 whitespace-nowrap min-h-[44px] backdrop-blur-xl"
        style={{
          backgroundColor: spec.surfaceGlass,
          border: `1px solid ${spec.cardBorder}`,
          boxShadow: spec.cardGlow,
          color: spec.textPrimary,
        }}
      >
        {spec.animeType === 'ITACHI' ? (
          <CrowIcon size={17} color={spec.primary} />
        ) : (
          <Palette size={17} style={{ color: spec.primary }} />
        )}
        <span>{spec.name}</span>
      </button>
    </header>
  );
};
