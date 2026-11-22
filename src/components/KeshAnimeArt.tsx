import React from 'react';
import { KeshThemeSpec } from '../theme/keshThemes';

export const SharinganArt: React.FC<{
  cx: number;
  cy: number;
  radius: number;
  isMangekyo?: boolean;
  isEternal?: boolean;
  uid?: string;
}> = ({ cx, cy, radius, isMangekyo = true, isEternal = false, uid = 'sh' }) => {
  const orbitRadius = radius * 0.56;
  const gradId = `sharingan-grad-${uid}`;
  return (
    <g>
      <defs>
        <radialGradient id={gradId} cx="50%" cy="50%" r="50%">
          <stop offset="0%" stopColor="#EF233C" />
          <stop offset="55%" stopColor="#991B1B" />
          <stop offset="100%" stopColor="#450A0A" />
        </radialGradient>
      </defs>
      <circle cx={cx} cy={cy} r={radius} fill={`url(#${gradId})`} />
      <circle
        cx={cx}
        cy={cy}
        r={radius}
        fill="none"
        stroke="#090507"
        strokeWidth={radius * 0.11}
      />
      <circle
        cx={cx}
        cy={cy}
        r={orbitRadius}
        fill="none"
        stroke="rgba(9,5,7,0.78)"
        strokeWidth={radius * 0.045}
      />
      {[0, 120, 240].map((angleDeg) => (
        <g key={angleDeg} transform={`rotate(${angleDeg - 90} ${cx} ${cy})`}>
          {isMangekyo && (
            <path
              d={`M ${cx} ${cy - radius * 0.16} Q ${cx + radius * 0.42} ${cy - radius * 0.45} ${cx} ${cy - radius * 0.86} Q ${cx + radius * 0.12} ${cy - radius * 0.45} ${cx} ${cy - radius * 0.16} Z`}
              fill="#090507"
            />
          )}
          {isEternal && (
            <path
              d={`M ${cx - radius * 0.14} ${cy - radius * 0.14} L ${cx} ${cy - radius * 0.82} L ${cx + radius * 0.14} ${cy - radius * 0.14} Z`}
              fill="none"
              stroke="#FF5722"
              strokeWidth={radius * 0.04}
            />
          )}
          <circle cx={cx} cy={cy - orbitRadius} r={radius * 0.13} fill="#090507" />
        </g>
      ))}
      <circle cx={cx} cy={cy} r={radius * 0.2} fill="#090507" />
      <circle cx={cx} cy={cy} r={radius * 0.06} fill="#EF233C" />
    </g>
  );
};

export const SixEyesArt: React.FC<{
  cx: number;
  cy: number;
  radius: number;
  accent?: string;
  uid?: string;
}> = ({ cx, cy, radius, accent = '#38BDF8', uid = 'six' }) => {
  const gradId = `sixeyes-grad-${uid}`;
  const rays = Array.from({ length: 12 }, (_, i) => {
    const rad = (i * 30 * Math.PI) / 180;
    return {
      x1: cx + radius * 0.28 * Math.cos(rad),
      y1: cy + radius * 0.28 * Math.sin(rad),
      x2: cx + radius * 0.85 * Math.cos(rad),
      y2: cy + radius * 0.85 * Math.sin(rad),
    };
  });

  return (
    <g>
      <defs>
        <radialGradient id={gradId} cx="50%" cy="50%" r="50%">
          <stop offset="0%" stopColor="#E0F2FE" />
          <stop offset="55%" stopColor={accent} />
          <stop offset="100%" stopColor="#1E3A8A" />
        </radialGradient>
      </defs>
      <circle cx={cx} cy={cy} r={radius} fill={`url(#${gradId})`} />
      <circle
        cx={cx}
        cy={cy}
        r={radius * 0.68}
        fill="none"
        stroke="#BAE6FD"
        strokeWidth={radius * 0.05}
      />
      {rays.map((ray, idx) => (
        <line
          key={idx}
          x1={ray.x1}
          y1={ray.y1}
          x2={ray.x2}
          y2={ray.y2}
          stroke="rgba(255,255,255,0.6)"
          strokeWidth={radius * 0.03}
        />
      ))}
      <circle cx={cx} cy={cy} r={radius * 0.22} fill="#060A17" />
    </g>
  );
};

export const WingsOfFreedomArt: React.FC<{
  cx: number;
  cy: number;
  radius: number;
  leftColor: string;
  rightColor: string;
}> = ({ cx, cy, radius, leftColor, rightColor }) => (
  <g>
    <path
      d={`M ${cx - radius * 0.05} ${cy + radius * 0.55} Q ${cx - radius * 0.85} ${cy + radius * 0.2} ${cx - radius * 0.72} ${cy - radius * 0.62} Q ${cx - radius * 0.25} ${cy - radius * 0.25} ${cx - radius * 0.05} ${cy + radius * 0.55} Z`}
      fill={leftColor}
    />
    <path
      d={`M ${cx + radius * 0.05} ${cy + radius * 0.55} Q ${cx + radius * 0.85} ${cy + radius * 0.2} ${cx + radius * 0.72} ${cy - radius * 0.62} Q ${cx + radius * 0.25} ${cy - radius * 0.25} ${cx + radius * 0.05} ${cy + radius * 0.55} Z`}
      fill={rightColor}
    />
  </g>
);

export const UchihaCrestIcon: React.FC<{ size?: number; className?: string }> = ({
  size = 24,
  className = '',
}) => (
  <svg width={size} height={size} viewBox="0 0 64 64" className={className} aria-hidden="true">
    <path
      d="M32 6 C17 6 8 17 8 30 C8 38 14 44 26 45 L26 57 L38 57 L38 45 C50 44 56 38 56 30 C56 17 47 6 32 6 Z"
      fill="#FFFFFF"
      stroke="#090507"
      strokeWidth="3"
    />
    <path
      d="M32 6 C17 6 8 17 8 30 C8 33 10 36 14 37 L50 37 C54 36 56 33 56 30 C56 17 47 6 32 6 Z"
      fill="#EF233C"
    />
    <rect x="28" y="45" width="8" height="12" rx="2" fill="#FFFFFF" stroke="#090507" strokeWidth="2" />
  </svg>
);

export const AkatsukiCloudSvg: React.FC<{ size?: number; className?: string }> = ({
  size = 38,
  className = '',
}) => (
  <svg width={size} height={size * 0.65} viewBox="0 0 120 78" className={className} aria-hidden="true">
    <path
      d="M28 60 C14 60 6 50 6 38 C6 27 15 19 26 20 C31 9 43 4 56 8 C66 4 81 9 86 21 C98 20 112 28 112 41 C112 52 103 60 90 60 Z"
      fill="#DC2626"
      stroke="#FFFFFF"
      strokeWidth="4"
      strokeLinejoin="round"
    />
    <path
      d="M42 36 C42 29 50 26 55 31"
      fill="none"
      stroke="#FFFFFF"
      strokeWidth="3"
      strokeLinecap="round"
    />
  </svg>
);

export const CrowIcon: React.FC<{ size?: number; color?: string; className?: string }> = ({
  size = 20,
  color = 'currentColor',
  className = '',
}) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden="true">
    <path
      d="M12 13 C8.5 7.5 4.5 6 2 8.5 C5 9 7.5 11.5 12 15.5 C16.5 11.5 19 9 22 8.5 C19.5 6 15.5 7.5 12 13 Z"
      fill={color}
    />
  </svg>
);

export const CharacterAddActionIcon: React.FC<{ spec: KeshThemeSpec; size?: number }> = ({
  spec,
  size = 24,
}) => {
  switch (spec.animeType) {
    case 'ITACHI':
      return <UchihaCrestIcon size={size} />;
    case 'MADARA':
      return (
        <svg width={size} height={size} viewBox="0 0 40 40" aria-hidden="true">
          <SharinganArt cx={20} cy={20} radius={17} isMangekyo isEternal uid="btn-madara" />
        </svg>
      );
    case 'GOJO':
      return (
        <svg width={size} height={size} viewBox="0 0 40 40" aria-hidden="true">
          <circle cx={15} cy={20} r={10} fill="none" stroke="#090C14" strokeWidth={2.8} />
          <circle cx={25} cy={20} r={10} fill="none" stroke="#090C14" strokeWidth={2.8} />
          <circle cx={20} cy={20} r={3.5} fill="#090C14" />
        </svg>
      );
    case 'YUTA':
      return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" aria-hidden="true">
          <path d="M19 5L5 19" strokeLinecap="round" />
          <path d="M15 5h4v4" strokeLinecap="round" strokeLinejoin="round" />
          <circle cx="8" cy="16" r="2.5" />
        </svg>
      );
    case 'MIKASA':
      return (
        <svg width={size} height={size} viewBox="0 0 40 40" aria-hidden="true">
          <WingsOfFreedomArt cx={20} cy={20} radius={17} leftColor="#FFFFFF" rightColor="#0F172A" />
        </svg>
      );
    case 'MARIN':
      return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
          <path d="M12 2l2.4 6.6L21 11l-6.6 2.4L12 20l-2.4-6.6L3 11l6.6-2.4L12 2z" />
        </svg>
      );
    case 'ALLURE':
      return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
          <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
        </svg>
      );
    default:
      return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" aria-hidden="true">
          <path d="M12 5v14M5 12h14" strokeLinecap="round" />
        </svg>
      );
  }
};

export const KeshAnimeAtmosphere: React.FC<{
  spec: KeshThemeSpec;
  activeBackgroundImage?: string;
  imagePosition?: string;
}> = ({ spec, activeBackgroundImage, imagePosition = 'center center' }) => {
  const bgSrc = activeBackgroundImage || spec.backgroundImage;
  if (!bgSrc) return null;

  return (
    <div
      className="pointer-events-none fixed inset-0 overflow-hidden z-0"
      aria-hidden="true"
    >
      <img
        src={bgSrc}
        alt={spec.characterTitle}
        referrerPolicy="no-referrer"
        className="w-full h-full object-cover opacity-95 transition-opacity duration-500"
        style={{ objectPosition: imagePosition }}
      />

      {/* Light breathable vignette so the wallpaper photo stays crisp behind frosted glass */}
      <div
        className="absolute inset-0"
        style={{
          background: `linear-gradient(180deg, ${spec.background}22 0%, ${spec.background}36 55%, ${spec.background}5E 100%)`,
        }}
      />

      {/* Character-specific crisp vector accents */}
      {spec.animeType === 'ITACHI' && (
        <svg viewBox="0 0 1000 1000" preserveAspectRatio="xMidYMid slice" className="absolute inset-0 w-full h-full">
          <g fill="rgba(239,35,60,0.22)">
            <path d="M150 220 Q120 190 90 210 Q125 215 150 240 Q175 215 210 210 Q180 190 150 220 Z" />
            <path d="M840 690 Q815 668 790 684 Q820 688 840 708 Q860 688 890 684 Q865 668 840 690 Z" />
          </g>
        </svg>
      )}
    </div>
  );
};

export const KeshLuxuryEmblem: React.FC<{
  spec: KeshThemeSpec;
  size?: number;
  className?: string;
}> = ({ spec, size = 42, className = '' }) => {
  const c = 50;
  const r = 46;
  const uid = `${spec.id}-emblem`;

  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 100 100"
      className={`shrink-0 ${className}`}
      aria-hidden="true"
    >
      {spec.animeType === 'ITACHI' && <SharinganArt cx={c} cy={c} radius={r} isMangekyo uid={uid} />}
      {spec.animeType === 'MADARA' && <SharinganArt cx={c} cy={c} radius={r} isMangekyo isEternal uid={uid} />}
      {spec.animeType === 'GOJO' && <SixEyesArt cx={c} cy={c} radius={r} accent="#38BDF8" uid={uid} />}
      {spec.animeType === 'YUTA' && <SixEyesArt cx={c} cy={c} radius={r} accent="#A855F7" uid={uid} />}
      {spec.animeType === 'MIKASA' && (
        <g>
          <circle cx={c} cy={c} r={r} fill="#13161C" stroke={spec.primary} strokeWidth={5} />
          <WingsOfFreedomArt cx={c} cy={c} radius={r * 0.78} leftColor="#FFFFFF" rightColor="#E11D48" />
        </g>
      )}
      {spec.animeType === 'MARIN' && (
        <g>
          <circle cx={c} cy={c} r={r} fill="#201021" stroke={spec.primary} strokeWidth={5} />
          <path
            d="M50 18 L58 42 L82 50 L58 58 L50 82 L42 58 L18 50 L42 42 Z"
            fill={spec.primary}
          />
          <circle cx={50} cy={50} r={10} fill={spec.secondary} />
        </g>
      )}
      {spec.animeType === 'ALLURE' && (
        <g>
          <circle cx={c} cy={c} r={r} fill="#1F0C20" stroke={spec.primary} strokeWidth={5} />
          <path
            d="M50 76 C50 76 23 58 23 40 C23 30 31 24 39 24 C44.5 24 48.5 27.5 50 31.5 C51.5 27.5 55.5 24 61 24 C69 24 77 30 77 40 C77 58 50 76 50 76 Z"
            fill={spec.primary}
          />
          <path d="M50 38 L54 47 L63 50 L54 53 L50 62 L46 53 L37 50 L46 47 Z" fill="#FCE7F3" />
        </g>
      )}
      {spec.animeType === 'NONE' && (
        <g>
          <circle cx={c} cy={c} r={48} fill={spec.surfaceVariant} />
          <circle cx={c} cy={c} r={r} fill="none" stroke={spec.primary} strokeWidth={5.5} />
          <path
            d="M 35 27 L 35 73 M 65 27 L 38 50 L 67 73"
            fill="none"
            stroke={spec.primary}
            strokeWidth={8.5}
            strokeLinecap="round"
            strokeLinejoin="round"
          />
          <circle cx={72} cy={28} r={5.5} fill={spec.secondary} />
        </g>
      )}
    </svg>
  );
};

export const ThemeEmblemBadge: React.FC<{
  spec: KeshThemeSpec;
  size?: number;
  className?: string;
}> = ({ spec, size = 64, className = '' }) => {
  const c = 50;
  const uid = `${spec.id}-hero-badge`;

  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 100 100"
      className={`shrink-0 ${className}`}
      aria-hidden="true"
    >
      <defs>
        <radialGradient id={`glow-${uid}`} cx="50%" cy="50%" r="50%">
          <stop offset="0%" stopColor={spec.primary} stopOpacity="0.45" />
          <stop offset="100%" stopColor={spec.primary} stopOpacity="0" />
        </radialGradient>
      </defs>
      <circle cx={c} cy={c} r={50} fill={`url(#glow-${uid})`} />

      {spec.animeType === 'ITACHI' && <SharinganArt cx={c} cy={c} radius={38} isMangekyo uid={uid} />}
      {spec.animeType === 'MADARA' && <SharinganArt cx={c} cy={c} radius={38} isMangekyo isEternal uid={uid} />}
      {spec.animeType === 'GOJO' && <SixEyesArt cx={c} cy={c} radius={38} accent="#38BDF8" uid={uid} />}
      {spec.animeType === 'YUTA' && <SixEyesArt cx={c} cy={c} radius={38} accent="#A855F7" uid={uid} />}
      {spec.animeType === 'MIKASA' && (
        <g>
          <circle cx={c} cy={c} r={38} fill={spec.surface} stroke={spec.primary} strokeWidth={4} />
          <WingsOfFreedomArt cx={c} cy={c} radius={29} leftColor="#FFFFFF" rightColor="#E11D48" />
        </g>
      )}
      {spec.animeType === 'MARIN' && (
        <g>
          <circle cx={c} cy={c} r={38} fill={spec.surface} stroke={spec.primary} strokeWidth={4} />
          <path d="M50 22 L56 44 L78 50 L56 56 L50 78 L44 56 L22 50 L44 44 Z" fill={spec.primary} />
        </g>
      )}
      {spec.animeType === 'ALLURE' && (
        <g>
          <circle cx={c} cy={c} r={38} fill={spec.surface} stroke={spec.primary} strokeWidth={4} />
          <path
            d="M50 72 C50 72 28 57 28 42 C28 34 34 29 41 29 C45.5 29 48.8 32 50 35 C51.2 32 54.5 29 59 29 C66 29 72 34 72 42 C72 57 50 72 50 72 Z"
            fill={spec.primary}
          />
        </g>
      )}
      {spec.animeType === 'NONE' && (
        <g>
          <circle cx={c} cy={c} r={38} fill={spec.surface} stroke={spec.primary} strokeWidth={4} />
          <circle cx={c} cy={c} r={16} fill={spec.primary} />
        </g>
      )}
    </svg>
  );
};

export const AnalyticsAnimeCenter: React.FC<{
  spec: KeshThemeSpec;
  cx: number;
  cy: number;
  innerRadius: number;
}> = ({ spec, cx, cy, innerRadius }) => {
  if (spec.animeType === 'NONE') return null;

  return (
    <g opacity={0.9}>
      {spec.animeType === 'ITACHI' && (
        <SharinganArt cx={cx} cy={cy} radius={innerRadius} isMangekyo uid="analytics-itachi" />
      )}
      {spec.animeType === 'MADARA' && (
        <SharinganArt cx={cx} cy={cy} radius={innerRadius} isMangekyo isEternal uid="analytics-madara" />
      )}
      {spec.animeType === 'GOJO' && (
        <g>
          <SixEyesArt cx={cx} cy={cy} radius={innerRadius} accent="#38BDF8" uid="analytics-gojo" />
          <circle cx={cx - innerRadius * 0.85} cy={cy} r={6} fill="#38BDF8" />
          <circle cx={cx + innerRadius * 0.85} cy={cy} r={6} fill="#EF4444" />
        </g>
      )}
      {spec.animeType === 'YUTA' && (
        <SixEyesArt cx={cx} cy={cy} radius={innerRadius} accent="#A855F7" uid="analytics-yuta" />
      )}
      {spec.animeType === 'MIKASA' && (
        <g>
          <circle cx={cx} cy={cy} r={innerRadius} fill="#13161C" />
          <WingsOfFreedomArt
            cx={cx}
            cy={cy}
            radius={innerRadius * 0.85}
            leftColor="rgba(255,255,255,0.4)"
            rightColor="rgba(225,29,72,0.55)"
          />
        </g>
      )}
      {spec.animeType === 'MARIN' && (
        <g>
          <circle cx={cx} cy={cy} r={innerRadius} fill="#201021" />
          <circle
            cx={cx}
            cy={cy}
            r={innerRadius * 0.72}
            fill="none"
            stroke="rgba(244,114,182,0.45)"
            strokeWidth={3}
          />
          <circle
            cx={cx}
            cy={cy}
            r={innerRadius * 0.45}
            fill="none"
            stroke="rgba(251,146,60,0.45)"
            strokeWidth={3}
          />
        </g>
      )}
      {spec.animeType === 'ALLURE' && (
        <g>
          <circle cx={cx} cy={cy} r={innerRadius} fill="#1F0C20" />
          <circle
            cx={cx}
            cy={cy}
            r={innerRadius * 0.74}
            fill="none"
            stroke="rgba(236,72,153,0.55)"
            strokeWidth={3.5}
          />
          <circle
            cx={cx}
            cy={cy}
            r={innerRadius * 0.45}
            fill="rgba(251,113,133,0.35)"
          />
        </g>
      )}
    </g>
  );
};
