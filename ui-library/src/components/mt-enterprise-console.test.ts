import { describe, expect, it } from 'vitest';
import './mt-enterprise-console.js';

describe('mt-enterprise-console', () => {
  it('renders the enterprise console shell', async () => {
    const element = document.createElement('mt-enterprise-console') as HTMLElement & {
      updateComplete?: Promise<unknown>;
      shadowRoot: ShadowRoot;
    };
    document.body.appendChild(element);
    if (element.updateComplete) {
      await element.updateComplete;
    } else {
      await new Promise((resolve) => requestAnimationFrame(resolve));
    }
    expect(element.shadowRoot?.querySelector('.app-shell')).toBeTruthy();
    element.remove();
  });
});
