import React, { useMemo, useState } from 'react';
import { CATEGORIES, CategoryId, formatSom, TransactionEntity, TransactionType } from '../data/models';
import { KeshThemeSpec, ThemeScene } from '../theme/keshThemes';
import { AkatsukiCloudSvg, AnalyticsAnimeCenter } from './KeshAnimeArt';
import { KeshTopHeader } from './KeshBrandComponents';

export const AnalyticsScreen: React.FC<{
  spec: KeshThemeSpec;
  activeScene: ThemeScene | null;
  userName: string;
  transactions: TransactionEntity[];
  onOpenThemes: () => void;
}> = ({ spec, activeScene, userName, transactions, onOpenThemes }) => {
  const [selectedType, setSelectedType] = useState<TransactionType>('EXPENSE');

  const filtered = useMemo(
    () => transactions.filter((t) => t.type === selectedType),
    [transactions, selectedType]
  );

  const totalSum = useMemo(
    () => filtered.reduce((acc, t) => acc + t.amount, 0),
    [filtered]
  );

  const categoryStats = useMemo(() => {
    const map = new Map<CategoryId, { sum: number; count: number }>();
    for (const tx of filtered) {
      const prev = map.get(tx.category) ?? { sum: 0, count: 0 };
      map.set(tx.category, { sum: prev.sum + tx.amount, count: prev.count + 1 });
    }
    return Array.from(map.entries())
      .map(([catId, data]) => ({
        category: CATEGORIES[catId] ?? CATEGORIES.OTHER_EXPENSE,
        sum: data.sum,
        count: data.count,
      }))
      .sort((a, b) => b.sum - a.sum);
  }, [filtered]);

  const sourceStats = useMemo(() => {
    const map = new Map<string, number>();
    for (const tx of transactions) {
      map.set(tx.bankName, (map.get(tx.bankName) ?? 0) + tx.amount);
    }
    return Array.from(map.entries())
      .map(([sourceName, sum]) => ({ sourceName, sum }))
      .sort((a, b) => b.sum - a.sum);
  }, [transactions]);

  const size = 220;
  const strokeWidth = 24;
  const radius = (size - strokeWidth) / 2;
  const circumference = 2 * Math.PI * radius;
  const innerRadius = radius - strokeWidth / 2 - 6;

  let cumulativeFraction = 0;

  return (
    <div className="relative z-10 pb-28 max-w-5xl mx-auto">
      <KeshTopHeader
        userName={userName}
        spec={spec}
        onOpenThemes={onOpenThemes}
      />

      <div className="px-4 sm:px-6 space-y-5">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div className="grid grid-cols-2 gap-3 w-full max-w-md">
            {(['EXPENSE', 'INCOME'] as TransactionType[]).map((type) => {
              const isSelected = selectedType === type;
              const label = type === 'EXPENSE' ? 'Расходы' : 'Доходы';
              return (
                <button
                  key={type}
                  type="button"
                  onClick={() => setSelectedType(type)}
                  className="py-3 rounded-2xl font-bold text-sm transition-colors cursor-pointer min-h-[44px] backdrop-blur-md"
                  style={{
                    backgroundColor: isSelected ? spec.primary : spec.surfaceGlass,
                    color: isSelected
                      ? spec.isLight
                        ? '#FFFFFF'
                        : '#090C14'
                      : spec.textPrimary,
                    border: `1px solid ${isSelected ? spec.primary : spec.cardBorder}`,
                    boxShadow: isSelected ? spec.cardGlow : 'none',
                  }}
                >
                  {label}
                </button>
              );
            })}
          </div>
          <span className="text-xs font-bold" style={{ color: spec.primary }}>
            {spec.analyticsLabel}
          </span>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-12 gap-5 items-start">
          {/* Donut Chart Card */}
          <div
            className="lg:col-span-5 rounded-3xl p-5 flex flex-col items-center backdrop-blur-md relative overflow-hidden"
            style={{
              backgroundColor: spec.surfaceGlass,
              border: `1px solid ${spec.cardBorder}`,
              boxShadow: spec.cardGlow,
            }}
          >
            {spec.animeType === 'ITACHI' && (
              <div className="absolute -left-2 -bottom-2 opacity-15 pointer-events-none">
                <AkatsukiCloudSvg size={76} />
              </div>
            )}

            {spec.isAnime && activeScene && (
              <div
                className="w-full rounded-2xl p-2.5 mb-4 flex items-center gap-3 relative z-10"
                style={{
                  backgroundColor: `${spec.background}CC`,
                  border: `1px solid ${spec.cardBorder}`,
                }}
              >
                <img
                  src={activeScene.image}
                  alt={`${spec.name} — ${activeScene.label}`}
                  referrerPolicy="no-referrer"
                  className="w-12 h-12 rounded-xl object-cover object-top shrink-0"
                />
                <div className="min-w-0 flex-1">
                  <div
                    className="text-xs font-bold truncate"
                    style={{ color: spec.primary }}
                  >
                    {spec.characterTitle}
                  </div>
                  <div
                    className="text-[11px] truncate"
                    style={{ color: spec.textSecondary }}
                  >
                    Сцена: {activeScene.label}
                  </div>
                </div>
              </div>
            )}

            {spec.isAnime && !activeScene && (
              <div
                className="text-xs font-bold mb-3 text-center relative z-10"
                style={{ color: spec.primary }}
              >
                {spec.motto}
              </div>
            )}

            <div className="relative w-[220px] h-[220px] flex items-center justify-center z-10">
              <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`}>
                <AnalyticsAnimeCenter
                  spec={spec}
                  cx={size / 2}
                  cy={size / 2}
                  innerRadius={innerRadius}
                />

                {totalSum <= 0 || categoryStats.length === 0 ? (
                  <circle
                    cx={size / 2}
                    cy={size / 2}
                    r={radius}
                    fill="none"
                    stroke={spec.surfaceVariant}
                    strokeWidth={strokeWidth}
                  />
                ) : (
                  <g transform={`rotate(-90 ${size / 2} ${size / 2})`}>
                    {categoryStats.map((stat, idx) => {
                      const fraction = stat.sum / totalSum;
                      const segLength = Math.max(fraction * circumference - 2, 1);
                      const dashOffset = -cumulativeFraction * circumference;
                      cumulativeFraction += fraction;
                      const segColor =
                        spec.animeType === 'ITACHI' && idx === 0
                          ? '#EF233C'
                          : stat.category.colorHex;
                      return (
                        <circle
                          key={stat.category.id}
                          cx={size / 2}
                          cy={size / 2}
                          r={radius}
                          fill="none"
                          stroke={segColor}
                          strokeWidth={strokeWidth}
                          strokeDasharray={`${segLength} ${circumference}`}
                          strokeDashoffset={dashOffset}
                        />
                      );
                    })}
                  </g>
                )}
              </svg>

              <div
                className="absolute px-3 py-1.5 rounded-xl text-center"
                style={{
                  backgroundColor: spec.isAnime ? `${spec.background}D9` : 'transparent',
                }}
              >
                <div className="text-[11px]" style={{ color: spec.textSecondary }}>
                  {selectedType === 'EXPENSE' ? 'Всего расход' : 'Всего доход'}
                </div>
                <div
                  className="text-sm font-extrabold tabular-nums"
                  style={{ color: spec.textPrimary }}
                >
                  {formatSom(totalSum)}
                </div>
              </div>
            </div>
          </div>

          {/* Categories & Sources Breakdown */}
          <div className="lg:col-span-7 space-y-3">
            {categoryStats.length === 0 ? (
              <div
                className="rounded-2xl p-6 text-center text-sm backdrop-blur-md"
                style={{
                  backgroundColor: spec.surfaceGlass,
                  color: spec.textSecondary,
                  border: `1px solid ${spec.cardBorder}`,
                }}
              >
                Нет операций в категории «{selectedType === 'EXPENSE' ? 'Расходы' : 'Доходы'}».
              </div>
            ) : (
              categoryStats.map(({ category, sum, count }, idx) => {
                const ratio = totalSum > 0 ? sum / totalSum : 0;
                const percent = Math.round(ratio * 100);
                const barColor =
                  spec.animeType === 'ITACHI' && idx === 0 ? spec.primary : category.colorHex;
                return (
                  <div
                    key={category.id}
                    className="rounded-2xl p-4 backdrop-blur-md"
                    style={{
                      backgroundColor: spec.surfaceGlass,
                      border: `1px solid ${spec.cardBorder}`,
                      boxShadow: spec.cardGlow,
                    }}
                  >
                    <div className="flex items-center justify-between gap-3">
                      <div className="flex items-center gap-2.5 min-w-0">
                        <span
                          className="w-3 h-3 rounded-full shrink-0"
                          style={{ backgroundColor: barColor }}
                        />
                        <div className="min-w-0">
                          <div
                            className="font-semibold text-sm truncate"
                            style={{ color: spec.textPrimary }}
                          >
                            {category.title}
                          </div>
                          <div className="text-xs" style={{ color: spec.textSecondary }}>
                            Операций: {count} · {percent}%
                          </div>
                        </div>
                      </div>
                      <span
                        className="font-bold text-sm tabular-nums whitespace-nowrap"
                        style={{ color: spec.textPrimary }}
                      >
                        {formatSom(sum)}
                      </span>
                    </div>

                    <div
                      className="w-full h-1.5 rounded-full overflow-hidden mt-2.5"
                      style={{ backgroundColor: spec.surfaceVariant }}
                    >
                      <div
                        className="h-full rounded-full transition-all duration-300"
                        style={{
                          width: `${ratio * 100}%`,
                          backgroundColor: barColor,
                        }}
                      />
                    </div>
                  </div>
                );
              })
            )}

            {sourceStats.length > 0 && (
              <div className="pt-3 space-y-2">
                <h3
                  className="font-bold text-base px-1"
                  style={{ color: spec.textPrimary }}
                >
                  Оборот по кошелькам и счетам
                </h3>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5">
                  {sourceStats.map((item) => (
                    <div
                      key={item.sourceName}
                      className="rounded-2xl px-4 py-3 flex items-center justify-between gap-3 backdrop-blur-md"
                      style={{
                        backgroundColor: spec.surfaceGlass,
                        border: `1px solid ${spec.cardBorder}`,
                      }}
                    >
                      <span
                        className="text-sm font-medium truncate"
                        style={{ color: spec.textPrimary }}
                      >
                        {item.sourceName}
                      </span>
                      <span
                        className="text-sm font-bold tabular-nums whitespace-nowrap"
                        style={{ color: spec.primary }}
                      >
                        {formatSom(item.sum)}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
