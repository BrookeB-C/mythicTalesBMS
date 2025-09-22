export type Severity = 'default' | 'warning' | 'alert';

export interface HeroCard {
  label: string;
  value: string;
  detail?: string;
  trend?: string;
}

export interface QueueItem {
  title: string;
  status: string;
  meta: string;
  severity: Severity;
}

export interface ActivityItem {
  time: string;
  summary: string;
  badge: string;
}

export interface DomainConsole {
  key: DomainKey;
  title: string;
  breadcrumb: string[];
  contexts: string[];
  whatChanged: string[];
  hero: HeroCard[];
  queue: QueueItem[];
  activity: ActivityItem[];
  quickActions: QuickAction[];
  quickMeta?: string;
}

export type DomainKey =
  | 'production'
  | 'prodinventory'
  | 'keginventory'
  | 'catalog'
  | 'taproom'
  | 'sales'
  | 'distribution'
  | 'procurement'
  | 'maintenance'
  | 'analytics'
  | 'billing'
  | 'compliance'
  | 'iam';

export type QuickAction = string | QuickActionDescriptor;

export interface QuickActionDescriptor {
  label: string;
  href?: string;
  message?: string;
  command?: QuickActionCommand;
}

export interface QuickActionCommand {
  type: string;
  kegId?: number;
  requiresVenue?: boolean;
}

export interface DomainGroup {
  id: string;
  label: string;
  domains: DomainKey[];
}

export const domainGroups: DomainGroup[] = [
  {
    id: 'operations',
    label: 'Operations',
    domains: ['production', 'prodinventory', 'keginventory', 'taproom']
  },
  {
    id: 'insights',
    label: 'Insights & Governance',
    domains: ['iam']
  }
];

export const defaultDomain: DomainKey = 'production';

export const enterpriseDomains: Record<DomainKey, DomainConsole> = {
  production: {
    key: 'production',
    title: 'Production',
    breadcrumb: ['Mythic Tales', 'Brewery Ops', 'Production'],
    contexts: ['Brewery: Mythic Central', 'Facility: Brewhouse A', 'Shift: Day'],
    whatChanged: [
      'CIP for Fermenter 4 overdue by 8 hours',
      'Cooling loop maintenance scheduled for tonight'
    ],
    hero: [
      { label: 'Batches Active', value: '4', detail: '2 conditioning', trend: '▲ 1 vs yesterday' },
      { label: 'Fermenter Capacity', value: '68%', detail: '6 of 18 vessels free' },
      { label: 'Upcoming Brews', value: '3', detail: 'Next: Hazy IPA 09:00' }
    ],
    queue: [
      {
        title: 'Batch #MT-87 · Mash In',
        status: 'In Progress',
        meta: 'Started 08:15 · Mash tun 2',
        severity: 'default'
      },
      {
        title: 'Batch #MT-86 · Transfer to FV4',
        status: 'Blocked',
        meta: 'Awaiting QC sign-off',
        severity: 'alert'
      },
      {
        title: 'Dry hop preparation',
        status: 'Ready',
        meta: 'Galaxy / Mosaic · 11:30',
        severity: 'warning'
      }
    ],
    activity: [
      {
        time: '07:45',
        summary: 'QA sample for Batch #MT-85 passed dissolved oxygen check',
        badge: 'QA'
      },
      { time: '07:10', summary: 'Boil kettle CIP completed', badge: 'Maintenance' },
      { time: '06:50', summary: 'Operator note: Pump cavitation resolved on line B', badge: 'Ops' }
    ],
    quickActions: ['Start Batch', 'Log Deviation', 'Schedule CIP', 'Assign Operator'],
    quickMeta: 'Favorite shortcuts based on last 30 days'
  },
  prodinventory: {
    key: 'prodinventory',
    title: 'Production Inventory',
    breadcrumb: ['Mythic Tales', 'Inventory', 'Production'],
    contexts: ['Brewery: Mythic Central', 'Warehouse: Raw A', 'View: Reorder risk'],
    whatChanged: [
      'Malt (Pilsner) dropped below reorder point by 2 pallets',
      'Lot #LA-204 pending lab release'
    ],
    hero: [
      {
        label: 'Stock vs Reorder',
        value: '82%',
        detail: '5 items at risk',
        trend: '▼ 6% week over week'
      },
      { label: 'WIP Lots', value: '12', detail: '4 awaiting QA' },
      { label: 'Shrinkage Rate', value: '1.6%', detail: 'Target 1.2%', trend: '▲ 0.3%' }
    ],
    queue: [
      {
        title: 'Receive Malt Delivery · PO #4521',
        status: 'Due 10:00',
        meta: 'Dock 2 · Requires tare weights',
        severity: 'default'
      },
      {
        title: 'Lot release · Yeast Prop 14',
        status: 'Waiting QA',
        meta: 'Lab review ETA 12:00',
        severity: 'warning'
      },
      {
        title: 'Cycle count · Hops Freezer',
        status: 'Blocked',
        meta: 'Freezer under maintenance',
        severity: 'alert'
      }
    ],
    activity: [
      { time: '07:55', summary: 'Issued 400kg Pilsner malt to Batch #MT-87', badge: 'Issue' },
      { time: '07:20', summary: 'Adjusted lot #CARA-33 by -5kg (spillage)', badge: 'Adjustment' },
      { time: '06:45', summary: 'Created transfer ticket for CO2 cylinders', badge: 'Transfer' }
    ],
    quickActions: ['Create Transfer', 'Adjust Inventory', 'Print Labels', 'Schedule Count'],
    quickMeta: 'Inventory coordinators · Top actions'
  },
  keginventory: {
    key: 'keginventory',
    title: 'Keg Inventory',
    breadcrumb: ['Mythic Tales', 'Logistics', 'Keg Inventory'],
    contexts: ['Region: Northwest', 'Depot: Portland', 'View: Turns'],
    whatChanged: ['12 kegs overdue for maintenance', 'Return shipment MT-RT-219 arrived 06:30'],
    hero: [
      { label: 'Kegs Available', value: '164', detail: '96×1/2 bbl · 68×1/6 bbl' },
      { label: 'Turn Time', value: '9.2 days', detail: 'Target ≤ 8 days', trend: '▲ 0.7d' },
      { label: 'Maintenance Overdue', value: '8%', detail: '12 of 150 in rotation', trend: '▲ 2%' }
    ],
    queue: [
      {
        title: 'Prep order · Taproom Downtown',
        status: 'Due 11:00',
        meta: '12 kegs · Load route PDX-3',
        severity: 'default'
      },
      {
        title: 'Maintenance · Valve replacement',
        status: 'In Progress',
        meta: 'Keg IDs 7721-7726',
        severity: 'warning'
      },
      {
        title: 'Return processing · Distributor NW',
        status: 'Ready',
        meta: 'Scan and sanitize 36 kegs',
        severity: 'default'
      }
    ],
    activity: [
      { time: '07:40', summary: 'Scanned keg #7718 → Assigned to Route PDX-2', badge: 'Scan' },
      { time: '07:15', summary: 'Marked keg #5521 as needs maintenance', badge: 'Maintenance' },
      { time: '06:55', summary: 'Completed tap swap · Taproom Pearl District', badge: 'Taproom' }
    ],
    quickActions: ['Scan Keg', 'Assign Route', 'Mark Maintenance', 'View Map'],
    quickMeta: 'Logistics shortcuts'
  },
  catalog: {
    key: 'catalog',
    title: 'Catalog',
    breadcrumb: ['Mythic Tales', 'Commercial', 'Catalog'],
    contexts: ['Season: Spring', 'Portfolio: Core', 'Compliance: On track'],
    whatChanged: [
      'Seasonal label artwork awaiting compliance review',
      'IPA SKU cost updated with new hop contract'
    ],
    hero: [
      { label: 'Active SKUs', value: '48', detail: '6 seasonal', trend: '▲ 2 new' },
      { label: 'Seasonal Readiness', value: '83%', detail: 'Artwork 1 pending' },
      { label: 'Compliance Checklist', value: '92%', detail: '2 items require update' }
    ],
    queue: [
      {
        title: 'Recipe approval · MT-IPA-2024',
        status: 'Review',
        meta: 'Needs product manager sign-off',
        severity: 'warning'
      },
      {
        title: 'Artwork review · Hazy Galaxy',
        status: 'In Progress',
        meta: 'Compliance check scheduled 13:00',
        severity: 'default'
      },
      {
        title: 'Distribution eligibility · Nitro Stout',
        status: 'Blocked',
        meta: 'Awaiting keg availability confirmation',
        severity: 'alert'
      }
    ],
    activity: [
      { time: '07:50', summary: 'Updated label spec for Golden Lager', badge: 'Label' },
      { time: '07:05', summary: 'Costing refreshed for Pumpkin Ale', badge: 'Costing' },
      { time: '06:40', summary: 'New SKU draft created: Citrus Wit', badge: 'SKU' }
    ],
    quickActions: ['Create SKU', 'Duplicate Recipe', 'Submit for Compliance', 'Open Asset Library'],
    quickMeta: 'Commercial toolkit'
  },
  taproom: {
    key: 'taproom',
    title: 'Taproom Operations',
    breadcrumb: ['Mythic Tales', 'Customer Experience', 'Taproom Operations'],
    contexts: ['Taproom: Pearl District', 'Shift: Morning', 'Mode: Service'],
    whatChanged: ['Keg blow risk on Tap #7 within 45 minutes', 'Event prep checklist due by 15:00'],
    hero: [
      { label: 'Sales Velocity', value: '$2.3k', detail: 'Last 4 hrs', trend: '▲ 9%' },
      { label: 'Keg Blow Risk', value: '3 taps', detail: 'Monitor taps 7, 11, 12' },
      { label: 'Staff Coverage', value: '100%', detail: '6 scheduled · 6 present' }
    ],
    queue: [
      {
        title: 'Ticket #482 · Table 14',
        status: 'Needs Response',
        meta: 'Guest waiting 3 min',
        severity: 'warning'
      },
      {
        title: 'Event prep · Trivia Night',
        status: 'Due 15:00',
        meta: 'Setup AV + reserved tables',
        severity: 'default'
      },
      {
        title: 'Keg changeover · Tap #11',
        status: 'In Progress',
        meta: 'Assign to Alex',
        severity: 'default'
      }
    ],
    activity: [
      { time: '07:55', summary: 'Ticket #478 resolved · Order comped', badge: 'Service' },
      { time: '07:25', summary: 'Logged pour anomaly on tap #4', badge: 'Analytics' },
      { time: '06:58', summary: 'Shift notes updated by Lead Sam', badge: 'Shift' }
    ],
    quickActions: ['Open Ticket', 'Trigger Keg Swap', 'Update Taplist', 'Broadcast Shift Note'],
    quickMeta: 'Taproom lead shortcuts'
  },
  sales: {
    key: 'sales',
    title: 'Sales',
    breadcrumb: ['Mythic Tales', 'Revenue', 'Sales'],
    contexts: ['Region: West', 'Segment: On-premise', 'Quarter: Q2'],
    whatChanged: [
      'Three offers expiring in 48 hours',
      'Distributor UrbanCraft flagged credit risk'
    ],
    hero: [
      {
        label: 'Bookings vs Target',
        value: '92%',
        detail: 'Target $1.2M · Booked $1.1M',
        trend: '▲ 4% WoW'
      },
      { label: 'Open Opportunities', value: '18', detail: '6 in commit' },
      { label: 'Fulfillment Status', value: '88%', detail: '4 orders need confirmation' }
    ],
    queue: [
      {
        title: 'Quote #Q-2091 · Copper Tap',
        status: 'Expires 24h',
        meta: 'Need pricing approval',
        severity: 'warning'
      },
      {
        title: 'Delivery confirmation · Hoppy Trails',
        status: 'Awaiting POD',
        meta: 'Driver ETA 10:30',
        severity: 'default'
      },
      {
        title: 'Opportunity · Riverfront Bistro',
        status: 'Next step: tasting',
        meta: 'Schedule by Friday',
        severity: 'default'
      }
    ],
    activity: [
      { time: '07:48', summary: 'Logged call with Barrel House', badge: 'Call' },
      { time: '07:18', summary: 'Order #SO-1187 updated shipping address', badge: 'Order' },
      { time: '06:55', summary: 'Credit hold added for UrbanCraft Distributing', badge: 'Finance' }
    ],
    quickActions: ['Create Order', 'Log Interaction', 'Schedule Tasting', 'Generate Quote'],
    quickMeta: 'Sales ops favorites'
  },
  distribution: {
    key: 'distribution',
    title: 'Distribution & Logistics',
    breadcrumb: ['Mythic Tales', 'Logistics', 'Distribution'],
    contexts: ['Hub: Portland', 'Fleet: Night Shift', 'View: Exceptions'],
    whatChanged: [
      'Route PDX-5 flagged temperature deviation',
      'Carrier SwiftCrate escalated delayed pickup'
    ],
    hero: [
      { label: 'Fleet Utilization', value: '76%', detail: '9 of 12 vehicles dispatched' },
      { label: 'On-time Delivery', value: '94%', detail: 'Goal ≥ 96%', trend: '▼ 1%' },
      { label: 'Route Exceptions', value: '3', detail: 'Temperature, delay, reroute' }
    ],
    queue: [
      {
        title: 'Dispatch · Route PDX-3',
        status: 'Loading',
        meta: 'Depart 09:30 · Driver Kim',
        severity: 'default'
      },
      {
        title: 'Optimize route · Seattle loop',
        status: 'Action needed',
        meta: 'Add new stop Hoppy Coop',
        severity: 'warning'
      },
      {
        title: 'Carrier escalation · SwiftCrate',
        status: 'High',
        meta: 'Escalate to manager',
        severity: 'alert'
      }
    ],
    activity: [
      { time: '07:52', summary: 'Proof of delivery uploaded for Route PDX-1', badge: 'POD' },
      { time: '07:33', summary: 'Route PDX-5 temperature alert acknowledged', badge: 'Alert' },
      { time: '06:59', summary: 'Driver note: Traffic congestion on I-5', badge: 'Driver' }
    ],
    quickActions: ['Assign Route', 'Notify Carrier', 'Print Manifests', 'Open Map View'],
    quickMeta: 'Dispatch center tools'
  },
  procurement: {
    key: 'procurement',
    title: 'Procurement',
    breadcrumb: ['Mythic Tales', 'Supply', 'Procurement'],
    contexts: ['Category: Packaging', 'Budget: FY24', 'View: Expiring contracts'],
    whatChanged: [
      'Supplier BrewPack OTIF dipped to 88%',
      'Contract with HopWorld expires in 30 days'
    ],
    hero: [
      { label: 'Spend vs Budget', value: '61%', detail: 'YTD' },
      { label: 'Supplier OTIF', value: '93%', detail: 'Target ≥ 95%', trend: '▼ 1%' },
      { label: 'Contracts Renewing', value: '5', detail: 'Next 45 days' }
    ],
    queue: [
      {
        title: 'RFQ · Crowler lids',
        status: 'Responses due 17:00',
        meta: '3 suppliers invited',
        severity: 'warning'
      },
      {
        title: 'PO approval · #PO-5524',
        status: 'Awaiting finance',
        meta: 'Total $18,400',
        severity: 'default'
      },
      {
        title: 'Contract renewal · HopWorld',
        status: 'Plan review',
        meta: 'Review pricing tiers',
        severity: 'default'
      }
    ],
    activity: [
      { time: '07:40', summary: 'Added supplier note for YeastWorks', badge: 'Supplier' },
      { time: '07:18', summary: 'Invoice mismatch flagged · Packaging tape', badge: 'Invoice' },
      { time: '06:47', summary: 'RFQ launched for glass growlers', badge: 'RFQ' }
    ],
    quickActions: ['Create PO', 'Launch RFQ', 'Record Supplier Note', 'View Contracts'],
    quickMeta: 'Strategic sourcing quick links'
  },
  maintenance: {
    key: 'maintenance',
    title: 'Maintenance',
    breadcrumb: ['Mythic Tales', 'Operations Support', 'Maintenance'],
    contexts: ['Facility: Brewhouse A', 'Crew: Day', 'Mode: Critical first'],
    whatChanged: [
      'Centrifuge vibration alert raised severity to critical',
      'Spare pump inventory low'
    ],
    hero: [
      { label: 'Equipment Uptime', value: '97.2%', detail: 'Rolling 30 days' },
      { label: 'Open Work Orders', value: '14', detail: '5 critical' },
      { label: 'PM Compliance', value: '91%', detail: 'Target ≥ 95%', trend: '▼ 2%' }
    ],
    queue: [
      {
        title: 'Work order · Centrifuge vibration',
        status: 'Critical',
        meta: 'Assign senior tech · ETA ASAP',
        severity: 'alert'
      },
      {
        title: 'PM · Packaging line lubrication',
        status: 'Due Today',
        meta: 'Task owner: Lee',
        severity: 'warning'
      },
      {
        title: 'Parts request · Pump seals',
        status: 'Waiting parts',
        meta: 'On order · ETA 2 days',
        severity: 'default'
      }
    ],
    activity: [
      {
        time: '07:58',
        summary: 'Technician Kim completed CIP skids inspection',
        badge: 'Complete'
      },
      { time: '07:30', summary: 'Logged downtime 12 min · Can filler jam', badge: 'Downtime' },
      { time: '06:55', summary: 'Condition monitoring alert · Glycol chiller', badge: 'Sensor' }
    ],
    quickActions: ['Create Work Order', 'Request Part', 'Log Downtime', 'Open Equipment Map'],
    quickMeta: 'Maintenance dispatch tools'
  },
  analytics: {
    key: 'analytics',
    title: 'Analytics',
    breadcrumb: ['Mythic Tales', 'Insights', 'Analytics'],
    contexts: ['Dashboard: Executive', 'Interval: Weekly', 'Compare: Last year'],
    whatChanged: [
      'Taproom performance index trending above forecast',
      'New predictive model ready for review'
    ],
    hero: [
      { label: 'Net Revenue', value: '$4.8M', detail: 'Week-to-date', trend: '▲ 6%' },
      { label: 'Brew Efficiency', value: '92.4%', detail: 'Goal 93%', trend: '▲ 0.5%' },
      { label: 'Taproom Index', value: '108', detail: 'Baseline 100', trend: '▲ 4' }
    ],
    queue: [
      {
        title: 'Report delivery · Executive pack',
        status: 'Scheduled',
        meta: 'Send 17:00 · Email & Teams',
        severity: 'default'
      },
      {
        title: 'Anomaly investigation · CO2 usage',
        status: 'Review',
        meta: 'Assign analyst',
        severity: 'warning'
      },
      {
        title: 'Predictive model · Sales mix v2',
        status: 'Ready',
        meta: 'Requires business sign-off',
        severity: 'default'
      }
    ],
    activity: [
      {
        time: '07:42',
        summary: 'Dashboard "Taproom pulse" updated with fresh data',
        badge: 'Dashboard'
      },
      { time: '07:12', summary: 'Alert dismissed · Keg loss variance normalized', badge: 'Alert' },
      { time: '06:58', summary: 'Forecast revised for July seasonal', badge: 'Forecast' }
    ],
    quickActions: ['Share Report', 'Annotate Metric', 'Configure Alert', 'Open Notebook'],
    quickMeta: 'Analytics workspace shortcuts'
  },
  billing: {
    key: 'billing',
    title: 'Billing & Finance',
    breadcrumb: ['Mythic Tales', 'Finance', 'Billing'],
    contexts: ['Entity: Mythic Tales', 'Aging: All', 'View: At risk'],
    whatChanged: [
      '3 accounts moved to 60-day bucket',
      'Cash flow improved with $120k payment received'
    ],
    hero: [
      { label: 'AR Aging', value: '$312k', detail: '>$60d: $48k', trend: '▼ $12k' },
      { label: 'Cash Flow', value: '$188k', detail: 'Week-to-date', trend: '▲ $32k' },
      { label: 'Unapplied Payments', value: '$9.4k', detail: '5 payments to reconcile' }
    ],
    queue: [
      {
        title: 'Invoice approval · INV-2041',
        status: 'Needs review',
        meta: 'Amount $18,200',
        severity: 'warning'
      },
      {
        title: 'Dispute · Barrel Bros',
        status: 'Escalated',
        meta: 'Open 12 days',
        severity: 'alert'
      },
      {
        title: 'Dunning · Hop City',
        status: 'Due today',
        meta: 'Send stage 2 notice',
        severity: 'default'
      }
    ],
    activity: [
      { time: '07:50', summary: 'Payment applied · Cascade Pub $12,400', badge: 'Payment' },
      { time: '07:25', summary: 'Credit memo created for Hoppy Trails', badge: 'Credit' },
      { time: '06:48', summary: 'Aging report exported to CSV', badge: 'Report' }
    ],
    quickActions: ['Issue Invoice', 'Apply Payment', 'Escalate Dispute', 'Open Aging Report'],
    quickMeta: 'Finance desk shortcuts'
  },
  compliance: {
    key: 'compliance',
    title: 'Compliance & QA',
    breadcrumb: ['Mythic Tales', 'Governance', 'Compliance'],
    contexts: ['Program: Federal', 'Audit: Spring', 'View: Incidents'],
    whatChanged: [
      'Label approval pending for Citrus Wit',
      'CAPA task for cleaning protocol due today'
    ],
    hero: [
      { label: 'Audit Readiness', value: '96%', detail: 'Docs verified' },
      { label: 'Permit Status', value: 'All active', detail: 'Next renewal 45 days' },
      { label: 'Incidents (30d)', value: '2', detail: '0 critical' }
    ],
    queue: [
      {
        title: 'File report · State excise',
        status: 'Due 17:00',
        meta: 'Requires finance review',
        severity: 'warning'
      },
      {
        title: 'CAPA · Cleaning SOP update',
        status: 'In Progress',
        meta: 'Owner: QA Lead',
        severity: 'default'
      },
      {
        title: 'Inspection prep checklist',
        status: 'Ready',
        meta: 'Brewery floor walkthrough',
        severity: 'default'
      }
    ],
    activity: [
      { time: '07:32', summary: 'Corrective action closed · Packaging deviation', badge: 'CAPA' },
      { time: '07:05', summary: 'Incident logged · Minor spill taproom', badge: 'Incident' },
      { time: '06:49', summary: 'Document updated · Allergen matrix', badge: 'Docs' }
    ],
    quickActions: ['File Report', 'Assign CAPA', 'Download Certificates', 'View Policies'],
    quickMeta: 'Compliance center shortcuts'
  },
  iam: {
    key: 'iam',
    title: 'Identity & Access',
    breadcrumb: ['Mythic Tales', 'Governance', 'Identity & Access'],
    contexts: ['Org: Mythic Tales', 'Review: Q2', 'Mode: Risk'],
    whatChanged: ['8 access reviews due within 24 hours', 'MFA adoption hit 92% goal'],
    hero: [
      { label: 'Active Users', value: '318', detail: '12 contractors' },
      { label: 'Pending Access Reviews', value: '8', detail: 'Owners: Dept leads', trend: '▼ 3' },
      { label: 'MFA Adoption', value: '92%', detail: 'Goal 92%', trend: '▲ 1%' }
    ],
    queue: [
      {
        title: 'Approve access · Taproom manager role',
        status: 'Due Today',
        meta: 'Request by Jamie Lee',
        severity: 'warning'
      },
      {
        title: 'Expiring permissions · Seasonal staff',
        status: 'Review',
        meta: '7 accounts expire Friday',
        severity: 'default'
      },
      {
        title: 'Policy violation · Admin login anomaly',
        status: 'Investigate',
        meta: 'Triggered by IAM monitor',
        severity: 'alert'
      }
    ],
    activity: [
      { time: '07:47', summary: 'Revoked session · Suspicious IP in EU', badge: 'Security' },
      {
        time: '07:16',
        summary: 'Role change · Added Distribution Planner role to Erin',
        badge: 'Role'
      },
      { time: '06:53', summary: 'Completed access review · Finance managers', badge: 'Review' }
    ],
    quickActions: ['Approve Access', 'Generate Report', 'Lock Account', 'Open Audit Trail'],
    quickMeta: 'Security ops shortcuts'
  }
};
