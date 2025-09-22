import { LitElement, html, css } from 'lit';
import { customElement, property, state } from 'lit/decorators.js';
import { classMap } from 'lit/directives/class-map.js';
import {
  enterpriseDomains,
  defaultDomain,
  domainGroups,
  type DomainKey,
  type DomainConsole,
  type QuickAction
} from '../data/enterprise-domains.js';

interface ToastItem {
  id: number;
  message: string;
}

@customElement('mt-enterprise-console')
export class MtEnterpriseConsole extends LitElement {
  @property({ type: Object })
  domains: Record<DomainKey, DomainConsole> = enterpriseDomains;

  @property({ type: String, attribute: 'selected-domain' })
  selectedDomain: DomainKey = defaultDomain;

  @state()
  private density: 'compact' | 'comfortable' = 'compact';

  @state()
  private drawerOpen = false;

  @state()
  private commandOpen = false;

  @state()
  private toasts: ToastItem[] = [];

  private toastId = 0;

  static styles = css`
    :host {
      display: block;
      color: #e2e8f0;
      font-family:
        'Inter',
        'Segoe UI',
        system-ui,
        -apple-system,
        BlinkMacSystemFont,
        sans-serif;
      --color-bg: #11161c;
      --color-surface: #1b232c;
      --color-surface-elevated: #212b36;
      --color-border: #2e3a46;
      --color-text: #e2e8f0;
      --color-text-muted: #94a3b8;
      --color-accent: #1b9aaa;
      --color-accent-soft: rgba(27, 154, 170, 0.2);
      --color-success: #5adb8b;
      --color-warning: #fbbf24;
      --color-alert: #ef5f5f;
      --color-focus: #38bdf8;
      --radius-sm: 4px;
      --radius-md: 8px;
      --radius-lg: 12px;
      --shadow-sm: 0 2px 4px rgba(0, 0, 0, 0.25);
      --shadow-md: 0 8px 18px rgba(0, 0, 0, 0.35);
      --top-bar-height: 56px;
      --nav-width: 240px;
      background: linear-gradient(135deg, #0d1117, #151c24 40%, #111820);
      min-height: 100vh;
    }

    :host([data-density='comfortable']) {
      --spacing-unit: 14px;
      --line-height: 1.6;
    }

    :host([data-density='compact']) {
      --spacing-unit: 10px;
      --line-height: 1.45;
    }

    *,
    *::before,
    *::after {
      box-sizing: border-box;
    }

    :host(:focus-visible) {
      outline: 2px solid var(--color-focus);
      outline-offset: 2px;
    }

    .app-shell {
      display: grid;
      grid-template-columns: var(--nav-width) 1fr;
      grid-template-rows: var(--top-bar-height) 1fr;
      min-height: 100vh;
    }

    .top-bar {
      grid-column: 1 / -1;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 24px;
      background: rgba(17, 24, 32, 0.85);
      backdrop-filter: blur(16px);
      box-shadow: var(--shadow-sm);
    }

    .top-bar__left,
    .top-bar__right {
      display: flex;
      align-items: center;
      gap: 16px;
    }

    .brand {
      font-weight: 600;
      letter-spacing: 0.04em;
      text-transform: uppercase;
      font-size: 0.95rem;
      color: var(--color-accent);
    }

    .command-button,
    .top-action,
    .secondary,
    .chip,
    .density-button,
    .quick-action,
    .nav-item {
      font: inherit;
      color: inherit;
      background: transparent;
      border: 1px solid var(--color-border);
      border-radius: var(--radius-sm);
      padding: 6px 12px;
      cursor: pointer;
      transition:
        background 0.2s ease,
        color 0.2s ease,
        border-color 0.2s ease;
    }

    .command-button,
    .top-action {
      background: rgba(33, 43, 54, 0.6);
    }

    .command-button:hover,
    .top-action:hover,
    .secondary:hover,
    .chip:hover,
    .density-button:hover,
    .quick-action:hover,
    .nav-item:hover {
      background: rgba(56, 189, 248, 0.12);
      border-color: rgba(56, 189, 248, 0.4);
    }

    .search-field input {
      background: rgba(15, 23, 42, 0.8);
      border: 1px solid var(--color-border);
      border-radius: var(--radius-sm);
      padding: 6px 10px;
      min-width: 240px;
      color: var(--color-text);
    }

    .search-field input::placeholder {
      color: var(--color-text-muted);
    }

    .user-chip {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 4px 10px 4px 4px;
      border: 1px solid var(--color-border);
      border-radius: var(--radius-lg);
      background: rgba(33, 43, 54, 0.6);
      cursor: pointer;
    }

    .avatar {
      width: 28px;
      height: 28px;
      border-radius: 50%;
      background: rgba(27, 154, 170, 0.3);
      display: inline-flex;
      align-items: center;
      justify-content: center;
      font-size: 0.8rem;
      font-weight: 600;
    }

    .nav-rail {
      grid-row: 2 / -1;
      background: rgba(17, 24, 32, 0.9);
      border-right: 1px solid var(--color-border);
      padding: 20px 12px;
      overflow-y: auto;
    }

    .nav-group {
      margin-bottom: 24px;
    }

    .nav-group h3 {
      font-size: 0.75rem;
      text-transform: uppercase;
      letter-spacing: 0.08em;
      color: var(--color-text-muted);
      margin: 0 0 8px;
    }

    .nav-group ul {
      list-style: none;
      margin: 0;
      padding: 0;
      display: flex;
      flex-direction: column;
      gap: 6px;
    }

    .nav-item {
      width: 100%;
      justify-content: flex-start;
      background: transparent;
      text-align: left;
    }

    .nav-item.active {
      background: var(--color-accent-soft);
      border-color: rgba(27, 154, 170, 0.5);
      color: #f8fafc;
    }

    .workspace {
      grid-row: 2 / -1;
      background: rgba(9, 14, 20, 0.85);
      padding: 24px 32px 32px;
      overflow-y: auto;
      display: flex;
      flex-direction: column;
      gap: 24px;
    }

    .breadcrumbs {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 0.85rem;
      color: var(--color-text-muted);
    }

    .breadcrumb {
      opacity: 0.8;
    }

    .breadcrumb.current {
      opacity: 1;
      font-weight: 600;
      color: var(--color-accent);
    }

    .breadcrumb-separator {
      opacity: 0.4;
    }

    .context-bar {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 12px;
    }

    .filter-chips {
      display: flex;
      gap: 8px;
      flex-wrap: wrap;
    }

    .chip {
      background: rgba(33, 43, 54, 0.7);
    }

    .density-toggle {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      color: var(--color-text-muted);
      font-size: 0.8rem;
    }

    .density-button.active {
      background: var(--color-accent-soft);
      border-color: rgba(27, 154, 170, 0.7);
      color: #f8fafc;
    }

    .what-changed {
      display: flex;
      gap: 16px;
      flex-wrap: wrap;
      background: rgba(239, 95, 95, 0.12);
      border: 1px solid rgba(239, 95, 95, 0.3);
      border-radius: var(--radius-md);
      padding: 12px 16px;
      font-size: 0.85rem;
    }

    .what-changed__item::before {
      content: '⚠';
      margin-right: 6px;
    }

    .hero {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .hero-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 16px;
    }

    .hero-card {
      background: var(--color-surface);
      border-radius: var(--radius-md);
      border: 1px solid var(--color-border);
      padding: 16px;
      box-shadow: var(--shadow-sm);
      display: grid;
      gap: 6px;
    }

    .hero-card__label {
      text-transform: uppercase;
      font-size: 0.7rem;
      letter-spacing: 0.08em;
      color: var(--color-text-muted);
    }

    .hero-card__value {
      font-size: 1.8rem;
      font-weight: 600;
    }

    .hero-card__detail {
      color: var(--color-text-muted);
      font-size: 0.85rem;
    }

    .hero-card__trend {
      font-size: 0.8rem;
      color: var(--color-success);
    }

    .workspace-grid {
      display: grid;
      grid-template-columns: minmax(340px, 40%) minmax(360px, 1fr);
      gap: 24px;
    }

    .pane {
      background: var(--color-surface);
      border-radius: var(--radius-md);
      border: 1px solid var(--color-border);
      box-shadow: var(--shadow-sm);
      display: flex;
      flex-direction: column;
    }

    .pane header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 20px;
      border-bottom: 1px solid var(--color-border);
    }

    .pane h2 {
      margin: 0;
      font-size: 1.05rem;
    }

    .queue-list,
    .activity-feed {
      list-style: none;
      margin: 0;
      padding: 0;
      display: flex;
      flex-direction: column;
    }

    .queue-item,
    .activity-item {
      padding: calc(var(--spacing-unit) * 1.1) 20px;
      border-bottom: 1px solid rgba(46, 58, 70, 0.55);
      display: grid;
      gap: 6px;
    }

    :host([data-density='compact']) .queue-item,
    :host([data-density='compact']) .activity-item {
      padding-top: calc(var(--spacing-unit) * 0.8);
      padding-bottom: calc(var(--spacing-unit) * 0.8);
    }

    :host([data-density='comfortable']) .queue-item,
    :host([data-density='comfortable']) .activity-item {
      padding-top: calc(var(--spacing-unit) * 1.1);
      padding-bottom: calc(var(--spacing-unit) * 1.1);
    }

    .queue-item:last-child,
    .activity-item:last-child {
      border-bottom: 0;
    }

    .queue-item__title {
      font-weight: 600;
      line-height: var(--line-height);
    }

    .queue-item__meta {
      color: var(--color-text-muted);
      font-size: 0.85rem;
    }

    .queue-item__status {
      justify-self: start;
      font-size: 0.75rem;
      text-transform: uppercase;
      letter-spacing: 0.08em;
      padding: 4px 8px;
      border-radius: var(--radius-sm);
      border: 1px solid transparent;
    }

    .queue-item--warning .queue-item__status {
      background: rgba(251, 191, 36, 0.15);
      border-color: rgba(251, 191, 36, 0.4);
      color: #facc15;
    }

    .queue-item--alert .queue-item__status {
      background: rgba(239, 95, 95, 0.15);
      border-color: rgba(239, 95, 95, 0.5);
      color: #f87171;
    }

    .queue-item--default .queue-item__status {
      background: rgba(27, 154, 170, 0.12);
      border-color: rgba(27, 154, 170, 0.5);
      color: var(--color-accent);
    }

    .activity-item {
      grid-template-columns: 64px 1fr auto;
      align-items: center;
    }

    .activity-item__time {
      font-size: 0.75rem;
      color: var(--color-text-muted);
    }

    .activity-item__summary {
      font-size: 0.9rem;
      line-height: var(--line-height);
    }

    .activity-item__badge {
      font-size: 0.7rem;
      text-transform: uppercase;
      letter-spacing: 0.08em;
      background: rgba(59, 130, 246, 0.2);
      color: #60a5fa;
      padding: 4px 6px;
      border-radius: var(--radius-sm);
    }

    .quick-actions {
      background: var(--color-surface);
      border-radius: var(--radius-md);
      border: 1px solid var(--color-border);
      box-shadow: var(--shadow-sm);
      padding: 16px 20px 24px;
    }

    .quick-actions header {
      display: flex;
      align-items: baseline;
      justify-content: space-between;
      margin-bottom: 16px;
    }

    .quick-actions h2 {
      margin: 0;
      font-size: 1.05rem;
    }

    .quick-actions__meta {
      color: var(--color-text-muted);
      font-size: 0.8rem;
    }

    .quick-actions__grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
      gap: 12px;
    }

    .quick-action {
      background: rgba(27, 154, 170, 0.1);
      border: 1px solid rgba(27, 154, 170, 0.5);
      border-radius: var(--radius-md);
      padding: 12px;
      font-weight: 600;
      text-align: left;
    }

    .side-drawer {
      position: fixed;
      top: var(--top-bar-height);
      right: -320px;
      width: 320px;
      height: calc(100vh - var(--top-bar-height));
      background: var(--color-surface-elevated);
      border-left: 1px solid var(--color-border);
      box-shadow: var(--shadow-md);
      display: flex;
      flex-direction: column;
      transition: right 0.3s ease;
      padding: 18px;
      z-index: 10;
    }

    .side-drawer--open {
      right: 0;
    }

    .side-drawer header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .drawer-body {
      margin-top: 18px;
      display: flex;
      flex-direction: column;
      gap: 20px;
      overflow-y: auto;
    }

    .drawer-body h3 {
      margin: 0 0 8px;
      font-size: 0.9rem;
    }

    .drawer-body label {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 0.85rem;
      color: var(--color-text-muted);
    }

    .command-overlay {
      position: fixed;
      inset: 0;
      background: rgba(5, 10, 20, 0.6);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 20;
      opacity: 0;
      pointer-events: none;
      transition: opacity 0.25s ease;
    }

    .command-overlay--open {
      opacity: 1;
      pointer-events: all;
    }

    .command-panel {
      background: var(--color-surface-elevated);
      border-radius: var(--radius-lg);
      border: 1px solid var(--color-border);
      box-shadow: var(--shadow-md);
      padding: 20px;
      width: min(560px, 90vw);
      display: flex;
      flex-direction: column;
      gap: 12px;
    }

    .command-panel header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .command-panel input {
      background: rgba(15, 23, 42, 0.8);
      border: 1px solid var(--color-border);
      border-radius: var(--radius-sm);
      padding: 8px 12px;
      color: var(--color-text);
    }

    .command-list {
      list-style: none;
      margin: 0;
      padding: 0;
      display: flex;
      flex-direction: column;
      gap: 6px;
      font-size: 0.9rem;
    }

    .command-list li {
      padding: 8px 10px;
      border-radius: var(--radius-sm);
      background: rgba(33, 43, 54, 0.6);
    }

    .toast-region {
      position: fixed;
      bottom: 24px;
      right: 24px;
      display: flex;
      flex-direction: column;
      gap: 12px;
      z-index: 30;
    }

    .toast {
      background: rgba(27, 154, 170, 0.92);
      color: #0e1726;
      padding: 12px 16px;
      border-radius: var(--radius-md);
      box-shadow: var(--shadow-md);
      transition:
        opacity 0.3s ease,
        transform 0.3s ease;
    }

    .toast--hide {
      opacity: 0;
      transform: translateY(8px);
    }

    .badge {
      background: rgba(248, 250, 252, 0.2);
      border-radius: 999px;
      padding: 2px 8px;
      font-size: 0.75rem;
    }

    @media (max-width: 1280px) {
      .workspace {
        padding: 20px;
      }

      .workspace-grid {
        grid-template-columns: 1fr;
      }
    }

    @media (max-width: 1024px) {
      .app-shell {
        grid-template-columns: 1fr;
      }

      .nav-rail {
        position: fixed;
        inset: var(--top-bar-height) 0 auto 0;
        height: auto;
        display: flex;
        overflow-x: auto;
        padding: 12px 16px;
        border-bottom: 1px solid var(--color-border);
      }

      .nav-rail nav {
        display: flex;
        gap: 24px;
      }

      .nav-group ul {
        flex-direction: row;
      }

      .nav-item {
        white-space: nowrap;
      }

      .workspace {
        margin-top: 160px;
      }
    }

    @media (max-width: 768px) {
      .top-bar {
        flex-wrap: wrap;
        padding: 12px 16px;
        height: auto;
      }

      .top-bar__left,
      .top-bar__right {
        width: 100%;
        justify-content: space-between;
      }

      .search-field input {
        width: 100%;
        min-width: 0;
      }
    }
  `;

  connectedCallback(): void {
    super.connectedCallback();
    window.addEventListener('keydown', this.handleKeydown);
    if (!this.selectedDomain) {
      this.selectedDomain = defaultDomain;
    }
    this.setAttribute('data-density', this.density);
  }

  disconnectedCallback(): void {
    super.disconnectedCallback();
    window.removeEventListener('keydown', this.handleKeydown);
  }

  protected updated(): void {
    this.setAttribute('data-density', this.density);
  }

  private get activeDomain(): DomainConsole {
    return this.domains[this.selectedDomain] ?? this.domains[defaultDomain];
  }

  private handleNavClick(domain: DomainKey): void {
    this.selectedDomain = domain;
  }

  private setDensity(density: 'compact' | 'comfortable'): void {
    this.density = density;
  }

  private toggleDrawer(): void {
    this.drawerOpen = !this.drawerOpen;
  }

  private closeDrawer(): void {
    this.drawerOpen = false;
  }

  private toggleCommandPalette(open?: boolean): void {
    const nextState = open ?? !this.commandOpen;
    this.commandOpen = nextState;
    if (nextState) {
      this.updateComplete.then(() => {
        const search = this.renderRoot.querySelector<HTMLInputElement>('#commandSearch');
        search?.focus();
      });
    } else {
      const trigger = this.renderRoot.querySelector<HTMLButtonElement>('#commandPaletteTrigger');
      trigger?.focus();
    }
  }

  private handleKeydown = (event: KeyboardEvent): void => {
    if ((event.metaKey || event.ctrlKey) && event.key.toLowerCase() === 'k') {
      event.preventDefault();
      this.toggleCommandPalette();
    }
    if (event.key === 'Escape') {
      if (this.commandOpen) {
        event.preventDefault();
        this.toggleCommandPalette(false);
      }
      if (this.drawerOpen) {
        event.preventDefault();
        this.closeDrawer();
      }
    }
  };

  private showToast(message: string): void {
    const id = ++this.toastId;
    const toast: ToastItem = { id, message };
    this.toasts = [...this.toasts, toast];
    setTimeout(() => this.dismissToast(id), 2500);
  }

  private dismissToast(id: number): void {
    const toastEl = this.renderRoot.querySelector<HTMLDivElement>(`#toast-${id}`);
    if (toastEl) {
      toastEl.classList.add('toast--hide');
    }
    setTimeout(() => {
      this.toasts = this.toasts.filter((toast) => toast.id !== id);
    }, 300);
  }

  private renderNav() {
    return domainGroups.map(
      (group) => html`
        <div class="nav-group" aria-labelledby="${group.id}-group">
          <h3 id="${group.id}-group">${group.label}</h3>
          <ul>
            ${group.domains.map((domain) => {
              const data = this.domains[domain];
              const isActive = this.selectedDomain === domain;
              return html`
                <li>
                  <button
                    class=${classMap({ 'nav-item': true, active: isActive })}
                    data-domain=${domain}
                    @click=${() => this.handleNavClick(domain)}
                    aria-current=${isActive ? 'page' : 'false'}
                  >
                    ${data.title}
                  </button>
                </li>
              `;
            })}
          </ul>
        </div>
      `
    );
  }

  private renderBreadcrumb() {
    const domain = this.activeDomain;
    const segments = [...domain.breadcrumb, domain.title];
    return segments.map((segment, index) => {
      const isLast = index === segments.length - 1;
      return html`
        <span class=${classMap({ breadcrumb: true, current: isLast })}>${segment}</span>
        ${!isLast ? html`<span class="breadcrumb-separator">›</span>` : null}
      `;
    });
  }

  private renderHeroCards() {
    return this.activeDomain.hero.map(
      (card) => html`
        <div class="hero-card">
          <span class="hero-card__label">${card.label}</span>
          <span class="hero-card__value">${card.value}</span>
          ${card.detail ? html`<span class="hero-card__detail">${card.detail}</span>` : null}
          ${card.trend ? html`<span class="hero-card__trend">${card.trend}</span>` : null}
        </div>
      `
    );
  }

  private renderQueue() {
    return this.activeDomain.queue.map(
      (item) => html`
        <li class=${classMap({ 'queue-item': true, [`queue-item--${item.severity}`]: true })}>
          <div class="queue-item__title">${item.title}</div>
          <div class="queue-item__meta">${item.meta}</div>
          <span class="queue-item__status">${item.status}</span>
        </li>
      `
    );
  }

  private renderActivity() {
    return this.activeDomain.activity.map(
      (item) => html`
        <li class="activity-item">
          <span class="activity-item__time">${item.time}</span>
          <div class="activity-item__summary">${item.summary}</div>
          <span class="activity-item__badge">${item.badge}</span>
        </li>
      `
    );
  }

  private renderQuickActions() {
    return this.activeDomain.quickActions.map((action) => {
      const descriptor = typeof action === 'string' ? { label: action } : action;

      const label = descriptor.label ?? '';
      const safeLabel = label || 'Action';
      const message = 'message' in descriptor && descriptor.message
        ? descriptor.message
        : `${safeLabel} queued`;
      const href = 'href' in descriptor ? descriptor.href : undefined;
      const command = descriptor.command as
        | { type: string; kegId?: number; requiresVenue?: boolean }
        | undefined;

      return html`
        <button
          class="quick-action"
          type="button"
          @click=${() => {
            if (command) {
              this.dispatchEvent(
                new CustomEvent('enterprise-command', {
                  detail: {
                    command,
                    label: safeLabel,
                    domain: this.selectedDomain
                  },
                  bubbles: true,
                  composed: true
                })
              );
              return;
            }
            if (href) {
              window.location.href = href;
              return;
            }
            this.showToast(message);
          }}
        >
          ${safeLabel}
        </button>
      `;
    });
  }

  private renderToasts() {
    return this.toasts.map(
      (toast) => html` <div class="toast" id=${`toast-${toast.id}`}>${toast.message}</div> `
    );
  }

  render() {
    const densityButtons = [
      { id: 'compact', label: 'Compact' },
      { id: 'comfortable', label: 'Comfortable' }
    ] as const;

    return html`
      <div class="app-shell" role="application" aria-label="Mythic Tales enterprise console">
        <header class="top-bar" role="banner">
          <div class="top-bar__left">
            <span class="brand">Mythic Tales BMS</span>
            <button
              class="command-button"
              type="button"
              aria-haspopup="true"
              aria-expanded=${this.commandOpen}
              id="commandPaletteTrigger"
              @click=${() => this.toggleCommandPalette()}
            >
              Command ⌘K
            </button>
            <div class="search-field" role="search">
              <input
                type="search"
                placeholder="Search batches, orders, people"
                aria-label="Global search"
              />
            </div>
          </div>
          <div class="top-bar__right">
            <button
              class="top-action"
              type="button"
              aria-controls="contextDrawer"
              aria-expanded=${this.drawerOpen}
              @click=${() => this.toggleDrawer()}
            >
              Filters
            </button>
            <button class="top-action" type="button">Help</button>
            <button class="top-action" type="button">
              Notifications <span class="badge" aria-label="2 unread">2</span>
            </button>
            <div class="user-chip" role="button" aria-haspopup="menu" aria-expanded="false">
              <span class="avatar" aria-hidden="true">BR</span>
              <span class="user-name">Brooke Rivera</span>
            </div>
          </div>
        </header>

        <aside class="nav-rail" aria-label="Primary navigation">
          <nav>${this.renderNav()}</nav>
        </aside>

        <main class="workspace" role="main">
          <div class="breadcrumbs" aria-label="Breadcrumb">${this.renderBreadcrumb()}</div>

          <div class="context-bar">
            <div class="filter-chips" role="group" aria-label="Context filters">
              ${this.activeDomain.contexts.map(
                (chip) => html`<button class="chip" type="button">${chip}</button>`
              )}
            </div>
            <div class="density-toggle" role="group" aria-label="Density toggle">
              <span>Density</span>
              ${densityButtons.map(
                ({ id, label }) => html`
                  <button
                    type="button"
                    class=${classMap({ 'density-button': true, active: this.density === id })}
                    data-density=${id}
                    @click=${() => this.setDensity(id)}
                  >
                    ${label}
                  </button>
                `
              )}
            </div>
          </div>

          <div class="what-changed" role="status" aria-live="polite">
            ${this.activeDomain.whatChanged.map(
              (item) => html`<span class="what-changed__item">${item}</span>`
            )}
          </div>

          <section class="hero" aria-label="Key performance indicators">
            <div class="hero-grid">${this.renderHeroCards()}</div>
          </section>

          <section class="workspace-grid">
            <section class="pane pane--queue" aria-label="Operational queue">
              <header>
                <h2>Operational Queue</h2>
                <button type="button" class="secondary">Sort</button>
              </header>
              <ul class="queue-list">
                ${this.renderQueue()}
              </ul>
            </section>
            <section class="pane pane--activity" aria-label="Recent activity">
              <header>
                <h2>Recent Activity</h2>
                <button type="button" class="secondary">Filter</button>
              </header>
              <ul class="activity-feed">
                ${this.renderActivity()}
              </ul>
            </section>
          </section>

          <section class="quick-actions" aria-label="Quick actions">
            <header>
              <h2>Quick Actions</h2>
              <div class="quick-actions__meta">${this.activeDomain.quickMeta ?? ''}</div>
            </header>
            <div class="quick-actions__grid">${this.renderQuickActions()}</div>
          </section>
        </main>

        <aside
          id="contextDrawer"
          class=${classMap({ 'side-drawer': true, 'side-drawer--open': this.drawerOpen })}
          aria-hidden=${!this.drawerOpen}
          aria-label="Filters and context"
        >
          <header>
            <h2>Context Controls</h2>
            <button type="button" class="secondary" @click=${() => this.closeDrawer()}>
              Close
            </button>
          </header>
          <div class="drawer-body">
            <section>
              <h3>Scopes</h3>
              <label><input type="checkbox" checked /> Include partner breweries</label>
              <label><input type="checkbox" /> Include archived records</label>
            </section>
            <section>
              <h3>Time Horizon</h3>
              <label><input type="radio" name="horizon" checked /> Today</label>
              <label><input type="radio" name="horizon" /> This week</label>
              <label><input type="radio" name="horizon" /> Custom…</label>
            </section>
            <section>
              <h3>Notifications</h3>
              <label><input type="checkbox" checked /> Surface escalations</label>
              <label><input type="checkbox" /> Include informational updates</label>
            </section>
          </div>
        </aside>

        <div
          class=${classMap({ 'command-overlay': true, 'command-overlay--open': this.commandOpen })}
          role="dialog"
          aria-modal="true"
          aria-labelledby="commandOverlayTitle"
          @click=${(event: Event) => {
            if (event.target === event.currentTarget) {
              this.toggleCommandPalette(false);
            }
          }}
        >
          <div class="command-panel">
            <header>
              <h2 id="commandOverlayTitle">Command Palette</h2>
              <button
                type="button"
                class="secondary"
                @click=${() => this.toggleCommandPalette(false)}
              >
                Close
              </button>
            </header>
            <input
              type="search"
              id="commandSearch"
              placeholder="Jump to domain, task, or record"
              aria-label="Command palette search"
            />
            <ul class="command-list">
              <li>Go to Taproom Operations → Open tickets</li>
              <li>Create order → Sales</li>
              <li>Open compliance dashboard</li>
            </ul>
          </div>
        </div>

        <div class="toast-region" aria-live="assertive" aria-atomic="true">
          ${this.renderToasts()}
        </div>
      </div>
    `;
  }
}
