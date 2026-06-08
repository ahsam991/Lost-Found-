---
name: Campus Connect Recovery
colors:
  surface: '#FFFFFF'
  surface-dim: '#d3daea'
  surface-bright: '#f9f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f0f3ff'
  surface-container: '#e7eefe'
  surface-container-high: '#e2e8f8'
  surface-container-highest: '#dce2f3'
  on-surface: '#151c27'
  on-surface-variant: '#434655'
  inverse-surface: '#2a313d'
  inverse-on-surface: '#ebf1ff'
  outline: '#737686'
  outline-variant: '#c3c6d7'
  surface-tint: '#0053db'
  primary: '#004ac6'
  on-primary: '#ffffff'
  primary-container: '#2563eb'
  on-primary-container: '#eeefff'
  inverse-primary: '#b4c5ff'
  secondary: '#006c49'
  on-secondary: '#ffffff'
  secondary-container: '#6cf8bb'
  on-secondary-container: '#00714d'
  tertiary: '#2c4bb9'
  on-tertiary: '#ffffff'
  tertiary-container: '#4865d4'
  on-tertiary-container: '#eff0ff'
  error: '#EF4444'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dbe1ff'
  primary-fixed-dim: '#b4c5ff'
  on-primary-fixed: '#00174b'
  on-primary-fixed-variant: '#003ea8'
  secondary-fixed: '#6ffbbe'
  secondary-fixed-dim: '#4edea3'
  on-secondary-fixed: '#002113'
  on-secondary-fixed-variant: '#005236'
  tertiary-fixed: '#dde1ff'
  tertiary-fixed-dim: '#b8c4ff'
  on-tertiary-fixed: '#001453'
  on-tertiary-fixed-variant: '#173bab'
  background: '#f9f9ff'
  on-background: '#151c27'
  surface-variant: '#dce2f3'
  warning: '#F59E0B'
  background-subtle: '#F9FAFB'
  border-light: '#E5E7EB'
  text-primary: '#111827'
  text-secondary: '#6B7280'
typography:
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
  headline-sm:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-md:
    fontFamily: JetBrains Mono
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
  label-sm:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
  2xl: 48px
  container-max: 1280px
  gutter: 16px
---

## Brand & Style

The design system is engineered for the **Smart Campus Lost & Found Management System**, focusing on a personality of **Trustworthiness, Efficiency, and Accessibility**. The system serves a diverse academic community, requiring a UI that feels dependable during stressful moments of loss and streamlined during the recovery process.

The chosen design style is **Corporate / Modern**, leaning into a systematic and clean execution. It prioritizes clarity through a structured grid, functional color application, and a refined "utility-first" aesthetic. The visual language avoids unnecessary decoration, instead using subtle elevation and intentional white space to guide users through the reporting and searching workflows with institutional confidence.

## Colors

This color palette is designed to instill a sense of security and institutional authority. The **Primary Blue** (#2563EB) is the core action color, used for high-priority interactions and brand presence. **Primary Dark** (#1E40AF) is utilized for hover states and deep headers to provide visual grounding.

**Secondary Green** (#10B981) signifies "Found" or "Resolved" statuses, creating a positive emotional loop. The system employs a rigorous semantic set for feedback: **Warning Orange** for items pending verification and **Error Red** for critical alerts. The neutral scale is biased toward a clean, cool gray to maintain a professional atmosphere against the soft **Background** (#F9FAFB) and crisp **Card Background** (#FFFFFF).

## Typography

The typography system utilizes **Inter** for all primary interfaces, ensuring high legibility and a modern, neutral tone. Headings are set in **Bold (700)** or **Semi-Bold (600)** to establish a clear information hierarchy, while body text uses the **Regular (400)** weight for optimal long-form reading on lost item descriptions.

**JetBrains Mono** is introduced for labels, ID numbers, and date stamps. This monospaced addition provides a functional, data-driven feel that distinguishes system-generated metadata (like Claim IDs or Category tags) from user-generated content. All type levels follow a strict vertical rhythm, with mobile-specific scaling for the largest headlines to maintain viewport integrity.

## Layout & Spacing

The system is built on an **8px base spacing grid**, providing a consistent rhythm across all components. For web applications, a **12-column fluid grid** is used with a maximum container width of 1280px. 

- **Desktop:** 12 columns, 24px margins, 16px gutters.
- **Tablet:** 8 columns, 24px margins, 16px gutters.
- **Mobile:** 4 columns, 16px margins, 12px gutters.

Spacing units should be used strictly: `sm` (8px) for internal component padding, `md` (16px) for spacing between related elements, and `lg` (24px) or `xl` (32px) for defining distinct sections of the layout.

## Elevation & Depth

This design system utilizes **Tonal Layers** combined with **Ambient Shadows** to define hierarchy. Depth is used sparingly to signify interactivity and importance.

- **Level 0 (Flat):** Used for the main background (#F9FAFB).
- **Level 1 (Card):** Used for item listings and content containers. Characterized by a subtle shadow: `0 1px 3px rgba(0,0,0,0.1)`. This level uses a 1px border (#E5E7EB) to maintain definition against the background.
- **Level 2 (Hover/Overlay):** Used for cards in a hover state or small dropdowns. The shadow increases to `0 4px 6px -1px rgba(0,0,0,0.1)`.
- **Level 3 (Modal):** High elevation for critical dialogs, using a deep diffused shadow and a backdrop blur of 8px to focus user attention.

## Shapes

The shape language is **Rounded**, reflecting an approachable yet professional environment. The standard radius for UI components like cards and input fields is **8px (md)**. 

Smaller elements like tags or checkboxes may use **4px (sm)** to appear sharper, while primary action buttons can occasionally utilize the **Full (9999px)** radius for a "Pill" style when they need to stand out as the singular primary action on a page. The consistency of the 8px radius across cards and containers creates a cohesive, modern container system.

## Components

### Buttons
- **Primary:** Background #2563EB, Text #FFFFFF, Weight 600. Focus state includes a 2px offset ring of #2563EB.
- **Secondary:** Background #F3F4F6, Text #111827. Used for "Cancel" or "View Details."
- **Tertiary/Ghost:** No background, #2563EB text. Used for low-priority actions in lists.

### Input Fields
Inputs use #FFFFFF background with a 1px #E5E7EB border. On focus, the border transitions to #2563EB with a subtle blue outer glow. Labels are Inter Semi-Bold 14px.

### Cards
Items in the "Found" gallery use the Level 1 shadow. They feature a top-aligned image (if available) with an 8px top-radius, followed by a padded section for the Item Title (Inter Bold) and Date (JetBrains Mono).

### Status Chips
Chips use a light tint of the semantic colors (e.g., Light Green background for "Found") with high-contrast dark text of the same hue to ensure WCAG 2.1 AA compliance.

### Progress Indicators
For the "Reporting" flow, use a linear step-indicator at the top of the container to reduce cognitive load, utilizing Primary Blue for completed steps.
