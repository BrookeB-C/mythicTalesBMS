import type { Meta, StoryObj } from '@storybook/web-components';
import { html } from 'lit';
import './mt-enterprise-console.js';
import { enterpriseDomains } from '../data/enterprise-domains.js';

type Story = StoryObj;

const meta: Meta = {
  title: 'Console/EnterpriseConsole',
  render: () =>
    html`<mt-enterprise-console
      style="min-height: 720px;"
      .domains=${enterpriseDomains}
    ></mt-enterprise-console>`,
  parameters: {
    layout: 'fullscreen'
  }
};

export default meta;

export const Default: Story = {};
