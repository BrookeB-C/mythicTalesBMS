/**
 * @license
 * Copyright 2019 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const U = globalThis, Q = U.ShadowRoot && (U.ShadyCSS === void 0 || U.ShadyCSS.nativeShadow) && "adoptedStyleSheets" in Document.prototype && "replace" in CSSStyleSheet.prototype, V = Symbol(), Y = /* @__PURE__ */ new WeakMap();
let de = class {
  constructor(e, t, a) {
    if (this._$cssResult$ = !0, a !== V) throw Error("CSSResult is not constructable. Use `unsafeCSS` or `css` instead.");
    this.cssText = e, this.t = t;
  }
  get styleSheet() {
    let e = this.o;
    const t = this.t;
    if (Q && e === void 0) {
      const a = t !== void 0 && t.length === 1;
      a && (e = Y.get(t)), e === void 0 && ((this.o = e = new CSSStyleSheet()).replaceSync(this.cssText), a && Y.set(t, e));
    }
    return e;
  }
  toString() {
    return this.cssText;
  }
};
const me = (i) => new de(typeof i == "string" ? i : i + "", void 0, V), ge = (i, ...e) => {
  const t = i.length === 1 ? i[0] : e.reduce((a, r, s) => a + ((o) => {
    if (o._$cssResult$ === !0) return o.cssText;
    if (typeof o == "number") return o;
    throw Error("Value passed to 'css' function must be a 'css' function result: " + o + ". Use 'unsafeCSS' to pass non-literal values, but take care to ensure page security.");
  })(r) + i[s + 1], i[0]);
  return new de(t, i, V);
}, be = (i, e) => {
  if (Q) i.adoptedStyleSheets = e.map((t) => t instanceof CSSStyleSheet ? t : t.styleSheet);
  else for (const t of e) {
    const a = document.createElement("style"), r = U.litNonce;
    r !== void 0 && a.setAttribute("nonce", r), a.textContent = t.cssText, i.appendChild(a);
  }
}, Z = Q ? (i) => i : (i) => i instanceof CSSStyleSheet ? ((e) => {
  let t = "";
  for (const a of e.cssRules) t += a.cssText;
  return me(t);
})(i) : i;
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const { is: ye, defineProperty: ve, getOwnPropertyDescriptor: fe, getOwnPropertyNames: xe, getOwnPropertySymbols: $e, getPrototypeOf: we } = Object, v = globalThis, ee = v.trustedTypes, _e = ee ? ee.emptyScript : "", H = v.reactiveElementPolyfillSupport, T = (i, e) => i, N = { toAttribute(i, e) {
  switch (e) {
    case Boolean:
      i = i ? _e : null;
      break;
    case Object:
    case Array:
      i = i == null ? i : JSON.stringify(i);
  }
  return i;
}, fromAttribute(i, e) {
  let t = i;
  switch (e) {
    case Boolean:
      t = i !== null;
      break;
    case Number:
      t = i === null ? null : Number(i);
      break;
    case Object:
    case Array:
      try {
        t = JSON.parse(i);
      } catch {
        t = null;
      }
  }
  return t;
} }, G = (i, e) => !ye(i, e), te = { attribute: !0, type: String, converter: N, reflect: !1, useDefault: !1, hasChanged: G };
Symbol.metadata ?? (Symbol.metadata = Symbol("metadata")), v.litPropertyMetadata ?? (v.litPropertyMetadata = /* @__PURE__ */ new WeakMap());
let C = class extends HTMLElement {
  static addInitializer(e) {
    this._$Ei(), (this.l ?? (this.l = [])).push(e);
  }
  static get observedAttributes() {
    return this.finalize(), this._$Eh && [...this._$Eh.keys()];
  }
  static createProperty(e, t = te) {
    if (t.state && (t.attribute = !1), this._$Ei(), this.prototype.hasOwnProperty(e) && ((t = Object.create(t)).wrapped = !0), this.elementProperties.set(e, t), !t.noAccessor) {
      const a = Symbol(), r = this.getPropertyDescriptor(e, a, t);
      r !== void 0 && ve(this.prototype, e, r);
    }
  }
  static getPropertyDescriptor(e, t, a) {
    const { get: r, set: s } = fe(this.prototype, e) ?? { get() {
      return this[t];
    }, set(o) {
      this[t] = o;
    } };
    return { get: r, set(o) {
      const l = r == null ? void 0 : r.call(this);
      s == null || s.call(this, o), this.requestUpdate(e, l, a);
    }, configurable: !0, enumerable: !0 };
  }
  static getPropertyOptions(e) {
    return this.elementProperties.get(e) ?? te;
  }
  static _$Ei() {
    if (this.hasOwnProperty(T("elementProperties"))) return;
    const e = we(this);
    e.finalize(), e.l !== void 0 && (this.l = [...e.l]), this.elementProperties = new Map(e.elementProperties);
  }
  static finalize() {
    if (this.hasOwnProperty(T("finalized"))) return;
    if (this.finalized = !0, this._$Ei(), this.hasOwnProperty(T("properties"))) {
      const t = this.properties, a = [...xe(t), ...$e(t)];
      for (const r of a) this.createProperty(r, t[r]);
    }
    const e = this[Symbol.metadata];
    if (e !== null) {
      const t = litPropertyMetadata.get(e);
      if (t !== void 0) for (const [a, r] of t) this.elementProperties.set(a, r);
    }
    this._$Eh = /* @__PURE__ */ new Map();
    for (const [t, a] of this.elementProperties) {
      const r = this._$Eu(t, a);
      r !== void 0 && this._$Eh.set(r, t);
    }
    this.elementStyles = this.finalizeStyles(this.styles);
  }
  static finalizeStyles(e) {
    const t = [];
    if (Array.isArray(e)) {
      const a = new Set(e.flat(1 / 0).reverse());
      for (const r of a) t.unshift(Z(r));
    } else e !== void 0 && t.push(Z(e));
    return t;
  }
  static _$Eu(e, t) {
    const a = t.attribute;
    return a === !1 ? void 0 : typeof a == "string" ? a : typeof e == "string" ? e.toLowerCase() : void 0;
  }
  constructor() {
    super(), this._$Ep = void 0, this.isUpdatePending = !1, this.hasUpdated = !1, this._$Em = null, this._$Ev();
  }
  _$Ev() {
    var e;
    this._$ES = new Promise((t) => this.enableUpdating = t), this._$AL = /* @__PURE__ */ new Map(), this._$E_(), this.requestUpdate(), (e = this.constructor.l) == null || e.forEach((t) => t(this));
  }
  addController(e) {
    var t;
    (this._$EO ?? (this._$EO = /* @__PURE__ */ new Set())).add(e), this.renderRoot !== void 0 && this.isConnected && ((t = e.hostConnected) == null || t.call(e));
  }
  removeController(e) {
    var t;
    (t = this._$EO) == null || t.delete(e);
  }
  _$E_() {
    const e = /* @__PURE__ */ new Map(), t = this.constructor.elementProperties;
    for (const a of t.keys()) this.hasOwnProperty(a) && (e.set(a, this[a]), delete this[a]);
    e.size > 0 && (this._$Ep = e);
  }
  createRenderRoot() {
    const e = this.shadowRoot ?? this.attachShadow(this.constructor.shadowRootOptions);
    return be(e, this.constructor.elementStyles), e;
  }
  connectedCallback() {
    var e;
    this.renderRoot ?? (this.renderRoot = this.createRenderRoot()), this.enableUpdating(!0), (e = this._$EO) == null || e.forEach((t) => {
      var a;
      return (a = t.hostConnected) == null ? void 0 : a.call(t);
    });
  }
  enableUpdating(e) {
  }
  disconnectedCallback() {
    var e;
    (e = this._$EO) == null || e.forEach((t) => {
      var a;
      return (a = t.hostDisconnected) == null ? void 0 : a.call(t);
    });
  }
  attributeChangedCallback(e, t, a) {
    this._$AK(e, a);
  }
  _$ET(e, t) {
    var s;
    const a = this.constructor.elementProperties.get(e), r = this.constructor._$Eu(e, a);
    if (r !== void 0 && a.reflect === !0) {
      const o = (((s = a.converter) == null ? void 0 : s.toAttribute) !== void 0 ? a.converter : N).toAttribute(t, a.type);
      this._$Em = e, o == null ? this.removeAttribute(r) : this.setAttribute(r, o), this._$Em = null;
    }
  }
  _$AK(e, t) {
    var s, o;
    const a = this.constructor, r = a._$Eh.get(e);
    if (r !== void 0 && this._$Em !== r) {
      const l = a.getPropertyOptions(r), n = typeof l.converter == "function" ? { fromAttribute: l.converter } : ((s = l.converter) == null ? void 0 : s.fromAttribute) !== void 0 ? l.converter : N;
      this._$Em = r;
      const c = n.fromAttribute(t, l.type);
      this[r] = c ?? ((o = this._$Ej) == null ? void 0 : o.get(r)) ?? c, this._$Em = null;
    }
  }
  requestUpdate(e, t, a) {
    var r;
    if (e !== void 0) {
      const s = this.constructor, o = this[e];
      if (a ?? (a = s.getPropertyOptions(e)), !((a.hasChanged ?? G)(o, t) || a.useDefault && a.reflect && o === ((r = this._$Ej) == null ? void 0 : r.get(e)) && !this.hasAttribute(s._$Eu(e, a)))) return;
      this.C(e, t, a);
    }
    this.isUpdatePending === !1 && (this._$ES = this._$EP());
  }
  C(e, t, { useDefault: a, reflect: r, wrapped: s }, o) {
    a && !(this._$Ej ?? (this._$Ej = /* @__PURE__ */ new Map())).has(e) && (this._$Ej.set(e, o ?? t ?? this[e]), s !== !0 || o !== void 0) || (this._$AL.has(e) || (this.hasUpdated || a || (t = void 0), this._$AL.set(e, t)), r === !0 && this._$Em !== e && (this._$Eq ?? (this._$Eq = /* @__PURE__ */ new Set())).add(e));
  }
  async _$EP() {
    this.isUpdatePending = !0;
    try {
      await this._$ES;
    } catch (t) {
      Promise.reject(t);
    }
    const e = this.scheduleUpdate();
    return e != null && await e, !this.isUpdatePending;
  }
  scheduleUpdate() {
    return this.performUpdate();
  }
  performUpdate() {
    var a;
    if (!this.isUpdatePending) return;
    if (!this.hasUpdated) {
      if (this.renderRoot ?? (this.renderRoot = this.createRenderRoot()), this._$Ep) {
        for (const [s, o] of this._$Ep) this[s] = o;
        this._$Ep = void 0;
      }
      const r = this.constructor.elementProperties;
      if (r.size > 0) for (const [s, o] of r) {
        const { wrapped: l } = o, n = this[s];
        l !== !0 || this._$AL.has(s) || n === void 0 || this.C(s, void 0, o, n);
      }
    }
    let e = !1;
    const t = this._$AL;
    try {
      e = this.shouldUpdate(t), e ? (this.willUpdate(t), (a = this._$EO) == null || a.forEach((r) => {
        var s;
        return (s = r.hostUpdate) == null ? void 0 : s.call(r);
      }), this.update(t)) : this._$EM();
    } catch (r) {
      throw e = !1, this._$EM(), r;
    }
    e && this._$AE(t);
  }
  willUpdate(e) {
  }
  _$AE(e) {
    var t;
    (t = this._$EO) == null || t.forEach((a) => {
      var r;
      return (r = a.hostUpdated) == null ? void 0 : r.call(a);
    }), this.hasUpdated || (this.hasUpdated = !0, this.firstUpdated(e)), this.updated(e);
  }
  _$EM() {
    this._$AL = /* @__PURE__ */ new Map(), this.isUpdatePending = !1;
  }
  get updateComplete() {
    return this.getUpdateComplete();
  }
  getUpdateComplete() {
    return this._$ES;
  }
  shouldUpdate(e) {
    return !0;
  }
  update(e) {
    this._$Eq && (this._$Eq = this._$Eq.forEach((t) => this._$ET(t, this[t]))), this._$EM();
  }
  updated(e) {
  }
  firstUpdated(e) {
  }
};
C.elementStyles = [], C.shadowRootOptions = { mode: "open" }, C[T("elementProperties")] = /* @__PURE__ */ new Map(), C[T("finalized")] = /* @__PURE__ */ new Map(), H == null || H({ ReactiveElement: C }), (v.reactiveElementVersions ?? (v.reactiveElementVersions = [])).push("2.1.1");
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const E = globalThis, B = E.trustedTypes, ie = B ? B.createPolicy("lit-html", { createHTML: (i) => i }) : void 0, ce = "$lit$", y = `lit$${Math.random().toFixed(9).slice(2)}$`, ue = "?" + y, Ae = `<${ue}>`, w = document, M = () => w.createComment(""), D = (i) => i === null || typeof i != "object" && typeof i != "function", X = Array.isArray, ke = (i) => X(i) || typeof (i == null ? void 0 : i[Symbol.iterator]) == "function", F = `[ 	
\f\r]`, P = /<(?:(!--|\/[^a-zA-Z])|(\/?[a-zA-Z][^>\s]*)|(\/?$))/g, ae = /-->/g, re = />/g, f = RegExp(`>|${F}(?:([^\\s"'>=/]+)(${F}*=${F}*(?:[^ 	
\f\r"'\`<>=]|("|')|))|$)`, "g"), se = /'/g, oe = /"/g, pe = /^(?:script|style|textarea|title)$/i, Ce = (i) => (e, ...t) => ({ _$litType$: i, strings: e, values: t }), h = Ce(1), _ = Symbol.for("lit-noChange"), u = Symbol.for("lit-nothing"), ne = /* @__PURE__ */ new WeakMap(), x = w.createTreeWalker(w, 129);
function he(i, e) {
  if (!X(i) || !i.hasOwnProperty("raw")) throw Error("invalid template strings array");
  return ie !== void 0 ? ie.createHTML(e) : e;
}
const Se = (i, e) => {
  const t = i.length - 1, a = [];
  let r, s = e === 2 ? "<svg>" : e === 3 ? "<math>" : "", o = P;
  for (let l = 0; l < t; l++) {
    const n = i[l];
    let c, p, d = -1, m = 0;
    for (; m < n.length && (o.lastIndex = m, p = o.exec(n), p !== null); ) m = o.lastIndex, o === P ? p[1] === "!--" ? o = ae : p[1] !== void 0 ? o = re : p[2] !== void 0 ? (pe.test(p[2]) && (r = RegExp("</" + p[2], "g")), o = f) : p[3] !== void 0 && (o = f) : o === f ? p[0] === ">" ? (o = r ?? P, d = -1) : p[1] === void 0 ? d = -2 : (d = o.lastIndex - p[2].length, c = p[1], o = p[3] === void 0 ? f : p[3] === '"' ? oe : se) : o === oe || o === se ? o = f : o === ae || o === re ? o = P : (o = f, r = void 0);
    const b = o === f && i[l + 1].startsWith("/>") ? " " : "";
    s += o === P ? n + Ae : d >= 0 ? (a.push(c), n.slice(0, d) + ce + n.slice(d) + y + b) : n + y + (d === -2 ? l : b);
  }
  return [he(i, s + (i[t] || "<?>") + (e === 2 ? "</svg>" : e === 3 ? "</math>" : "")), a];
};
class R {
  constructor({ strings: e, _$litType$: t }, a) {
    let r;
    this.parts = [];
    let s = 0, o = 0;
    const l = e.length - 1, n = this.parts, [c, p] = Se(e, t);
    if (this.el = R.createElement(c, a), x.currentNode = this.el.content, t === 2 || t === 3) {
      const d = this.el.content.firstChild;
      d.replaceWith(...d.childNodes);
    }
    for (; (r = x.nextNode()) !== null && n.length < l; ) {
      if (r.nodeType === 1) {
        if (r.hasAttributes()) for (const d of r.getAttributeNames()) if (d.endsWith(ce)) {
          const m = p[o++], b = r.getAttribute(d).split(y), I = /([.?@])?(.*)/.exec(m);
          n.push({ type: 1, index: s, name: I[2], strings: b, ctor: I[1] === "." ? Te : I[1] === "?" ? Ee : I[1] === "@" ? Oe : z }), r.removeAttribute(d);
        } else d.startsWith(y) && (n.push({ type: 6, index: s }), r.removeAttribute(d));
        if (pe.test(r.tagName)) {
          const d = r.textContent.split(y), m = d.length - 1;
          if (m > 0) {
            r.textContent = B ? B.emptyScript : "";
            for (let b = 0; b < m; b++) r.append(d[b], M()), x.nextNode(), n.push({ type: 2, index: ++s });
            r.append(d[m], M());
          }
        }
      } else if (r.nodeType === 8) if (r.data === ue) n.push({ type: 2, index: s });
      else {
        let d = -1;
        for (; (d = r.data.indexOf(y, d + 1)) !== -1; ) n.push({ type: 7, index: s }), d += y.length - 1;
      }
      s++;
    }
  }
  static createElement(e, t) {
    const a = w.createElement("template");
    return a.innerHTML = e, a;
  }
}
function S(i, e, t = i, a) {
  var o, l;
  if (e === _) return e;
  let r = a !== void 0 ? (o = t._$Co) == null ? void 0 : o[a] : t._$Cl;
  const s = D(e) ? void 0 : e._$litDirective$;
  return (r == null ? void 0 : r.constructor) !== s && ((l = r == null ? void 0 : r._$AO) == null || l.call(r, !1), s === void 0 ? r = void 0 : (r = new s(i), r._$AT(i, t, a)), a !== void 0 ? (t._$Co ?? (t._$Co = []))[a] = r : t._$Cl = r), r !== void 0 && (e = S(i, r._$AS(i, e.values), r, a)), e;
}
class Pe {
  constructor(e, t) {
    this._$AV = [], this._$AN = void 0, this._$AD = e, this._$AM = t;
  }
  get parentNode() {
    return this._$AM.parentNode;
  }
  get _$AU() {
    return this._$AM._$AU;
  }
  u(e) {
    const { el: { content: t }, parts: a } = this._$AD, r = ((e == null ? void 0 : e.creationScope) ?? w).importNode(t, !0);
    x.currentNode = r;
    let s = x.nextNode(), o = 0, l = 0, n = a[0];
    for (; n !== void 0; ) {
      if (o === n.index) {
        let c;
        n.type === 2 ? c = new q(s, s.nextSibling, this, e) : n.type === 1 ? c = new n.ctor(s, n.name, n.strings, this, e) : n.type === 6 && (c = new Me(s, this, e)), this._$AV.push(c), n = a[++l];
      }
      o !== (n == null ? void 0 : n.index) && (s = x.nextNode(), o++);
    }
    return x.currentNode = w, r;
  }
  p(e) {
    let t = 0;
    for (const a of this._$AV) a !== void 0 && (a.strings !== void 0 ? (a._$AI(e, a, t), t += a.strings.length - 2) : a._$AI(e[t])), t++;
  }
}
class q {
  get _$AU() {
    var e;
    return ((e = this._$AM) == null ? void 0 : e._$AU) ?? this._$Cv;
  }
  constructor(e, t, a, r) {
    this.type = 2, this._$AH = u, this._$AN = void 0, this._$AA = e, this._$AB = t, this._$AM = a, this.options = r, this._$Cv = (r == null ? void 0 : r.isConnected) ?? !0;
  }
  get parentNode() {
    let e = this._$AA.parentNode;
    const t = this._$AM;
    return t !== void 0 && (e == null ? void 0 : e.nodeType) === 11 && (e = t.parentNode), e;
  }
  get startNode() {
    return this._$AA;
  }
  get endNode() {
    return this._$AB;
  }
  _$AI(e, t = this) {
    e = S(this, e, t), D(e) ? e === u || e == null || e === "" ? (this._$AH !== u && this._$AR(), this._$AH = u) : e !== this._$AH && e !== _ && this._(e) : e._$litType$ !== void 0 ? this.$(e) : e.nodeType !== void 0 ? this.T(e) : ke(e) ? this.k(e) : this._(e);
  }
  O(e) {
    return this._$AA.parentNode.insertBefore(e, this._$AB);
  }
  T(e) {
    this._$AH !== e && (this._$AR(), this._$AH = this.O(e));
  }
  _(e) {
    this._$AH !== u && D(this._$AH) ? this._$AA.nextSibling.data = e : this.T(w.createTextNode(e)), this._$AH = e;
  }
  $(e) {
    var s;
    const { values: t, _$litType$: a } = e, r = typeof a == "number" ? this._$AC(e) : (a.el === void 0 && (a.el = R.createElement(he(a.h, a.h[0]), this.options)), a);
    if (((s = this._$AH) == null ? void 0 : s._$AD) === r) this._$AH.p(t);
    else {
      const o = new Pe(r, this), l = o.u(this.options);
      o.p(t), this.T(l), this._$AH = o;
    }
  }
  _$AC(e) {
    let t = ne.get(e.strings);
    return t === void 0 && ne.set(e.strings, t = new R(e)), t;
  }
  k(e) {
    X(this._$AH) || (this._$AH = [], this._$AR());
    const t = this._$AH;
    let a, r = 0;
    for (const s of e) r === t.length ? t.push(a = new q(this.O(M()), this.O(M()), this, this.options)) : a = t[r], a._$AI(s), r++;
    r < t.length && (this._$AR(a && a._$AB.nextSibling, r), t.length = r);
  }
  _$AR(e = this._$AA.nextSibling, t) {
    var a;
    for ((a = this._$AP) == null ? void 0 : a.call(this, !1, !0, t); e !== this._$AB; ) {
      const r = e.nextSibling;
      e.remove(), e = r;
    }
  }
  setConnected(e) {
    var t;
    this._$AM === void 0 && (this._$Cv = e, (t = this._$AP) == null || t.call(this, e));
  }
}
class z {
  get tagName() {
    return this.element.tagName;
  }
  get _$AU() {
    return this._$AM._$AU;
  }
  constructor(e, t, a, r, s) {
    this.type = 1, this._$AH = u, this._$AN = void 0, this.element = e, this.name = t, this._$AM = r, this.options = s, a.length > 2 || a[0] !== "" || a[1] !== "" ? (this._$AH = Array(a.length - 1).fill(new String()), this.strings = a) : this._$AH = u;
  }
  _$AI(e, t = this, a, r) {
    const s = this.strings;
    let o = !1;
    if (s === void 0) e = S(this, e, t, 0), o = !D(e) || e !== this._$AH && e !== _, o && (this._$AH = e);
    else {
      const l = e;
      let n, c;
      for (e = s[0], n = 0; n < s.length - 1; n++) c = S(this, l[a + n], t, n), c === _ && (c = this._$AH[n]), o || (o = !D(c) || c !== this._$AH[n]), c === u ? e = u : e !== u && (e += (c ?? "") + s[n + 1]), this._$AH[n] = c;
    }
    o && !r && this.j(e);
  }
  j(e) {
    e === u ? this.element.removeAttribute(this.name) : this.element.setAttribute(this.name, e ?? "");
  }
}
class Te extends z {
  constructor() {
    super(...arguments), this.type = 3;
  }
  j(e) {
    this.element[this.name] = e === u ? void 0 : e;
  }
}
class Ee extends z {
  constructor() {
    super(...arguments), this.type = 4;
  }
  j(e) {
    this.element.toggleAttribute(this.name, !!e && e !== u);
  }
}
class Oe extends z {
  constructor(e, t, a, r, s) {
    super(e, t, a, r, s), this.type = 5;
  }
  _$AI(e, t = this) {
    if ((e = S(this, e, t, 0) ?? u) === _) return;
    const a = this._$AH, r = e === u && a !== u || e.capture !== a.capture || e.once !== a.once || e.passive !== a.passive, s = e !== u && (a === u || r);
    r && this.element.removeEventListener(this.name, this, a), s && this.element.addEventListener(this.name, this, e), this._$AH = e;
  }
  handleEvent(e) {
    var t;
    typeof this._$AH == "function" ? this._$AH.call(((t = this.options) == null ? void 0 : t.host) ?? this.element, e) : this._$AH.handleEvent(e);
  }
}
class Me {
  constructor(e, t, a) {
    this.element = e, this.type = 6, this._$AN = void 0, this._$AM = t, this.options = a;
  }
  get _$AU() {
    return this._$AM._$AU;
  }
  _$AI(e) {
    S(this, e);
  }
}
const j = E.litHtmlPolyfillSupport;
j == null || j(R, q), (E.litHtmlVersions ?? (E.litHtmlVersions = [])).push("3.3.1");
const De = (i, e, t) => {
  const a = (t == null ? void 0 : t.renderBefore) ?? e;
  let r = a._$litPart$;
  if (r === void 0) {
    const s = (t == null ? void 0 : t.renderBefore) ?? null;
    a._$litPart$ = r = new q(e.insertBefore(M(), s), s, void 0, t ?? {});
  }
  return r._$AI(i), r;
};
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const $ = globalThis;
let O = class extends C {
  constructor() {
    super(...arguments), this.renderOptions = { host: this }, this._$Do = void 0;
  }
  createRenderRoot() {
    var t;
    const e = super.createRenderRoot();
    return (t = this.renderOptions).renderBefore ?? (t.renderBefore = e.firstChild), e;
  }
  update(e) {
    const t = this.render();
    this.hasUpdated || (this.renderOptions.isConnected = this.isConnected), super.update(e), this._$Do = De(t, this.renderRoot, this.renderOptions);
  }
  connectedCallback() {
    var e;
    super.connectedCallback(), (e = this._$Do) == null || e.setConnected(!0);
  }
  disconnectedCallback() {
    var e;
    super.disconnectedCallback(), (e = this._$Do) == null || e.setConnected(!1);
  }
  render() {
    return _;
  }
};
var le;
O._$litElement$ = !0, O.finalized = !0, (le = $.litElementHydrateSupport) == null || le.call($, { LitElement: O });
const W = $.litElementPolyfillSupport;
W == null || W({ LitElement: O });
($.litElementVersions ?? ($.litElementVersions = [])).push("4.2.1");
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const Re = (i) => (e, t) => {
  t !== void 0 ? t.addInitializer(() => {
    customElements.define(i, e);
  }) : customElements.define(i, e);
};
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const qe = { attribute: !0, type: String, converter: N, reflect: !1, hasChanged: G }, Ie = (i = qe, e, t) => {
  const { kind: a, metadata: r } = t;
  let s = globalThis.litPropertyMetadata.get(r);
  if (s === void 0 && globalThis.litPropertyMetadata.set(r, s = /* @__PURE__ */ new Map()), a === "setter" && ((i = Object.create(i)).wrapped = !0), s.set(t.name, i), a === "accessor") {
    const { name: o } = t;
    return { set(l) {
      const n = e.get.call(this);
      e.set.call(this, l), this.requestUpdate(o, n, i);
    }, init(l) {
      return l !== void 0 && this.C(o, void 0, i, l), l;
    } };
  }
  if (a === "setter") {
    const { name: o } = t;
    return function(l) {
      const n = this[o];
      e.call(this, l), this.requestUpdate(o, n, i);
    };
  }
  throw Error("Unsupported decorator location: " + a);
};
function J(i) {
  return (e, t) => typeof t == "object" ? Ie(i, e, t) : ((a, r, s) => {
    const o = r.hasOwnProperty(s);
    return r.constructor.createProperty(s, a), o ? Object.getOwnPropertyDescriptor(r, s) : void 0;
  })(i, e, t);
}
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
function L(i) {
  return J({ ...i, state: !0, attribute: !1 });
}
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const Ue = { ATTRIBUTE: 1 }, Ne = (i) => (...e) => ({ _$litDirective$: i, values: e });
class Be {
  constructor(e) {
  }
  get _$AU() {
    return this._$AM._$AU;
  }
  _$AT(e, t, a) {
    this._$Ct = e, this._$AM = t, this._$Ci = a;
  }
  _$AS(e, t) {
    return this.update(e, t);
  }
  update(e, t) {
    return this.render(...t);
  }
}
/**
 * @license
 * Copyright 2018 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const k = Ne(class extends Be {
  constructor(i) {
    var e;
    if (super(i), i.type !== Ue.ATTRIBUTE || i.name !== "class" || ((e = i.strings) == null ? void 0 : e.length) > 2) throw Error("`classMap()` can only be used in the `class` attribute and must be the only part in the attribute.");
  }
  render(i) {
    return " " + Object.keys(i).filter((e) => i[e]).join(" ") + " ";
  }
  update(i, [e]) {
    var a, r;
    if (this.st === void 0) {
      this.st = /* @__PURE__ */ new Set(), i.strings !== void 0 && (this.nt = new Set(i.strings.join(" ").split(/\s/).filter((s) => s !== "")));
      for (const s in e) e[s] && !((a = this.nt) != null && a.has(s)) && this.st.add(s);
      return this.render(e);
    }
    const t = i.element.classList;
    for (const s of this.st) s in e || (t.remove(s), this.st.delete(s));
    for (const s in e) {
      const o = !!e[s];
      o === this.st.has(s) || (r = this.nt) != null && r.has(s) || (o ? (t.add(s), this.st.add(s)) : (t.remove(s), this.st.delete(s)));
    }
    return _;
  }
}), ze = [
  {
    id: "operations",
    label: "Operations",
    domains: ["production", "prodinventory", "keginventory", "taproom"]
  },
  {
    id: "insights",
    label: "Insights & Governance",
    domains: ["iam"]
  }
], K = "production", Le = {
  production: {
    key: "production",
    title: "Production",
    breadcrumb: ["Mythic Tales", "Brewery Ops", "Production"],
    contexts: ["Brewery: Mythic Central", "Facility: Brewhouse A", "Shift: Day"],
    whatChanged: [
      "CIP for Fermenter 4 overdue by 8 hours",
      "Cooling loop maintenance scheduled for tonight"
    ],
    hero: [
      { label: "Batches Active", value: "4", detail: "2 conditioning", trend: "▲ 1 vs yesterday" },
      { label: "Fermenter Capacity", value: "68%", detail: "6 of 18 vessels free" },
      { label: "Upcoming Brews", value: "3", detail: "Next: Hazy IPA 09:00" }
    ],
    queue: [
      {
        title: "Batch #MT-87 · Mash In",
        status: "In Progress",
        meta: "Started 08:15 · Mash tun 2",
        severity: "default"
      },
      {
        title: "Batch #MT-86 · Transfer to FV4",
        status: "Blocked",
        meta: "Awaiting QC sign-off",
        severity: "alert"
      },
      {
        title: "Dry hop preparation",
        status: "Ready",
        meta: "Galaxy / Mosaic · 11:30",
        severity: "warning"
      }
    ],
    activity: [
      {
        time: "07:45",
        summary: "QA sample for Batch #MT-85 passed dissolved oxygen check",
        badge: "QA"
      },
      { time: "07:10", summary: "Boil kettle CIP completed", badge: "Maintenance" },
      { time: "06:50", summary: "Operator note: Pump cavitation resolved on line B", badge: "Ops" }
    ],
    quickActions: ["Start Batch", "Log Deviation", "Schedule CIP", "Assign Operator"],
    quickMeta: "Favorite shortcuts based on last 30 days"
  },
  prodinventory: {
    key: "prodinventory",
    title: "Production Inventory",
    breadcrumb: ["Mythic Tales", "Inventory", "Production"],
    contexts: ["Brewery: Mythic Central", "Warehouse: Raw A", "View: Reorder risk"],
    whatChanged: [
      "Malt (Pilsner) dropped below reorder point by 2 pallets",
      "Lot #LA-204 pending lab release"
    ],
    hero: [
      {
        label: "Stock vs Reorder",
        value: "82%",
        detail: "5 items at risk",
        trend: "▼ 6% week over week"
      },
      { label: "WIP Lots", value: "12", detail: "4 awaiting QA" },
      { label: "Shrinkage Rate", value: "1.6%", detail: "Target 1.2%", trend: "▲ 0.3%" }
    ],
    queue: [
      {
        title: "Receive Malt Delivery · PO #4521",
        status: "Due 10:00",
        meta: "Dock 2 · Requires tare weights",
        severity: "default"
      },
      {
        title: "Lot release · Yeast Prop 14",
        status: "Waiting QA",
        meta: "Lab review ETA 12:00",
        severity: "warning"
      },
      {
        title: "Cycle count · Hops Freezer",
        status: "Blocked",
        meta: "Freezer under maintenance",
        severity: "alert"
      }
    ],
    activity: [
      { time: "07:55", summary: "Issued 400kg Pilsner malt to Batch #MT-87", badge: "Issue" },
      { time: "07:20", summary: "Adjusted lot #CARA-33 by -5kg (spillage)", badge: "Adjustment" },
      { time: "06:45", summary: "Created transfer ticket for CO2 cylinders", badge: "Transfer" }
    ],
    quickActions: ["Create Transfer", "Adjust Inventory", "Print Labels", "Schedule Count"],
    quickMeta: "Inventory coordinators · Top actions"
  },
  keginventory: {
    key: "keginventory",
    title: "Keg Inventory",
    breadcrumb: ["Mythic Tales", "Logistics", "Keg Inventory"],
    contexts: ["Region: Northwest", "Depot: Portland", "View: Turns"],
    whatChanged: ["12 kegs overdue for maintenance", "Return shipment MT-RT-219 arrived 06:30"],
    hero: [
      { label: "Kegs Available", value: "164", detail: "96×1/2 bbl · 68×1/6 bbl" },
      { label: "Turn Time", value: "9.2 days", detail: "Target ≤ 8 days", trend: "▲ 0.7d" },
      { label: "Maintenance Overdue", value: "8%", detail: "12 of 150 in rotation", trend: "▲ 2%" }
    ],
    queue: [
      {
        title: "Prep order · Taproom Downtown",
        status: "Due 11:00",
        meta: "12 kegs · Load route PDX-3",
        severity: "default"
      },
      {
        title: "Maintenance · Valve replacement",
        status: "In Progress",
        meta: "Keg IDs 7721-7726",
        severity: "warning"
      },
      {
        title: "Return processing · Distributor NW",
        status: "Ready",
        meta: "Scan and sanitize 36 kegs",
        severity: "default"
      }
    ],
    activity: [
      { time: "07:40", summary: "Scanned keg #7718 → Assigned to Route PDX-2", badge: "Scan" },
      { time: "07:15", summary: "Marked keg #5521 as needs maintenance", badge: "Maintenance" },
      { time: "06:55", summary: "Completed tap swap · Taproom Pearl District", badge: "Taproom" }
    ],
    quickActions: ["Scan Keg", "Assign Route", "Mark Maintenance", "View Map"],
    quickMeta: "Logistics shortcuts"
  },
  catalog: {
    key: "catalog",
    title: "Catalog",
    breadcrumb: ["Mythic Tales", "Commercial", "Catalog"],
    contexts: ["Season: Spring", "Portfolio: Core", "Compliance: On track"],
    whatChanged: [
      "Seasonal label artwork awaiting compliance review",
      "IPA SKU cost updated with new hop contract"
    ],
    hero: [
      { label: "Active SKUs", value: "48", detail: "6 seasonal", trend: "▲ 2 new" },
      { label: "Seasonal Readiness", value: "83%", detail: "Artwork 1 pending" },
      { label: "Compliance Checklist", value: "92%", detail: "2 items require update" }
    ],
    queue: [
      {
        title: "Recipe approval · MT-IPA-2024",
        status: "Review",
        meta: "Needs product manager sign-off",
        severity: "warning"
      },
      {
        title: "Artwork review · Hazy Galaxy",
        status: "In Progress",
        meta: "Compliance check scheduled 13:00",
        severity: "default"
      },
      {
        title: "Distribution eligibility · Nitro Stout",
        status: "Blocked",
        meta: "Awaiting keg availability confirmation",
        severity: "alert"
      }
    ],
    activity: [
      { time: "07:50", summary: "Updated label spec for Golden Lager", badge: "Label" },
      { time: "07:05", summary: "Costing refreshed for Pumpkin Ale", badge: "Costing" },
      { time: "06:40", summary: "New SKU draft created: Citrus Wit", badge: "SKU" }
    ],
    quickActions: ["Create SKU", "Duplicate Recipe", "Submit for Compliance", "Open Asset Library"],
    quickMeta: "Commercial toolkit"
  },
  taproom: {
    key: "taproom",
    title: "Taproom Operations",
    breadcrumb: ["Mythic Tales", "Customer Experience", "Taproom Operations"],
    contexts: ["Taproom: Pearl District", "Shift: Morning", "Mode: Service"],
    whatChanged: ["Keg blow risk on Tap #7 within 45 minutes", "Event prep checklist due by 15:00"],
    hero: [
      { label: "Sales Velocity", value: "$2.3k", detail: "Last 4 hrs", trend: "▲ 9%" },
      { label: "Keg Blow Risk", value: "3 taps", detail: "Monitor taps 7, 11, 12" },
      { label: "Staff Coverage", value: "100%", detail: "6 scheduled · 6 present" }
    ],
    queue: [
      {
        title: "Ticket #482 · Table 14",
        status: "Needs Response",
        meta: "Guest waiting 3 min",
        severity: "warning"
      },
      {
        title: "Event prep · Trivia Night",
        status: "Due 15:00",
        meta: "Setup AV + reserved tables",
        severity: "default"
      },
      {
        title: "Keg changeover · Tap #11",
        status: "In Progress",
        meta: "Assign to Alex",
        severity: "default"
      }
    ],
    activity: [
      { time: "07:55", summary: "Ticket #478 resolved · Order comped", badge: "Service" },
      { time: "07:25", summary: "Logged pour anomaly on tap #4", badge: "Analytics" },
      { time: "06:58", summary: "Shift notes updated by Lead Sam", badge: "Shift" }
    ],
    quickActions: ["Open Ticket", "Trigger Keg Swap", "Update Taplist", "Broadcast Shift Note"],
    quickMeta: "Taproom lead shortcuts"
  },
  sales: {
    key: "sales",
    title: "Sales",
    breadcrumb: ["Mythic Tales", "Revenue", "Sales"],
    contexts: ["Region: West", "Segment: On-premise", "Quarter: Q2"],
    whatChanged: [
      "Three offers expiring in 48 hours",
      "Distributor UrbanCraft flagged credit risk"
    ],
    hero: [
      {
        label: "Bookings vs Target",
        value: "92%",
        detail: "Target $1.2M · Booked $1.1M",
        trend: "▲ 4% WoW"
      },
      { label: "Open Opportunities", value: "18", detail: "6 in commit" },
      { label: "Fulfillment Status", value: "88%", detail: "4 orders need confirmation" }
    ],
    queue: [
      {
        title: "Quote #Q-2091 · Copper Tap",
        status: "Expires 24h",
        meta: "Need pricing approval",
        severity: "warning"
      },
      {
        title: "Delivery confirmation · Hoppy Trails",
        status: "Awaiting POD",
        meta: "Driver ETA 10:30",
        severity: "default"
      },
      {
        title: "Opportunity · Riverfront Bistro",
        status: "Next step: tasting",
        meta: "Schedule by Friday",
        severity: "default"
      }
    ],
    activity: [
      { time: "07:48", summary: "Logged call with Barrel House", badge: "Call" },
      { time: "07:18", summary: "Order #SO-1187 updated shipping address", badge: "Order" },
      { time: "06:55", summary: "Credit hold added for UrbanCraft Distributing", badge: "Finance" }
    ],
    quickActions: ["Create Order", "Log Interaction", "Schedule Tasting", "Generate Quote"],
    quickMeta: "Sales ops favorites"
  },
  distribution: {
    key: "distribution",
    title: "Distribution & Logistics",
    breadcrumb: ["Mythic Tales", "Logistics", "Distribution"],
    contexts: ["Hub: Portland", "Fleet: Night Shift", "View: Exceptions"],
    whatChanged: [
      "Route PDX-5 flagged temperature deviation",
      "Carrier SwiftCrate escalated delayed pickup"
    ],
    hero: [
      { label: "Fleet Utilization", value: "76%", detail: "9 of 12 vehicles dispatched" },
      { label: "On-time Delivery", value: "94%", detail: "Goal ≥ 96%", trend: "▼ 1%" },
      { label: "Route Exceptions", value: "3", detail: "Temperature, delay, reroute" }
    ],
    queue: [
      {
        title: "Dispatch · Route PDX-3",
        status: "Loading",
        meta: "Depart 09:30 · Driver Kim",
        severity: "default"
      },
      {
        title: "Optimize route · Seattle loop",
        status: "Action needed",
        meta: "Add new stop Hoppy Coop",
        severity: "warning"
      },
      {
        title: "Carrier escalation · SwiftCrate",
        status: "High",
        meta: "Escalate to manager",
        severity: "alert"
      }
    ],
    activity: [
      { time: "07:52", summary: "Proof of delivery uploaded for Route PDX-1", badge: "POD" },
      { time: "07:33", summary: "Route PDX-5 temperature alert acknowledged", badge: "Alert" },
      { time: "06:59", summary: "Driver note: Traffic congestion on I-5", badge: "Driver" }
    ],
    quickActions: ["Assign Route", "Notify Carrier", "Print Manifests", "Open Map View"],
    quickMeta: "Dispatch center tools"
  },
  procurement: {
    key: "procurement",
    title: "Procurement",
    breadcrumb: ["Mythic Tales", "Supply", "Procurement"],
    contexts: ["Category: Packaging", "Budget: FY24", "View: Expiring contracts"],
    whatChanged: [
      "Supplier BrewPack OTIF dipped to 88%",
      "Contract with HopWorld expires in 30 days"
    ],
    hero: [
      { label: "Spend vs Budget", value: "61%", detail: "YTD" },
      { label: "Supplier OTIF", value: "93%", detail: "Target ≥ 95%", trend: "▼ 1%" },
      { label: "Contracts Renewing", value: "5", detail: "Next 45 days" }
    ],
    queue: [
      {
        title: "RFQ · Crowler lids",
        status: "Responses due 17:00",
        meta: "3 suppliers invited",
        severity: "warning"
      },
      {
        title: "PO approval · #PO-5524",
        status: "Awaiting finance",
        meta: "Total $18,400",
        severity: "default"
      },
      {
        title: "Contract renewal · HopWorld",
        status: "Plan review",
        meta: "Review pricing tiers",
        severity: "default"
      }
    ],
    activity: [
      { time: "07:40", summary: "Added supplier note for YeastWorks", badge: "Supplier" },
      { time: "07:18", summary: "Invoice mismatch flagged · Packaging tape", badge: "Invoice" },
      { time: "06:47", summary: "RFQ launched for glass growlers", badge: "RFQ" }
    ],
    quickActions: ["Create PO", "Launch RFQ", "Record Supplier Note", "View Contracts"],
    quickMeta: "Strategic sourcing quick links"
  },
  maintenance: {
    key: "maintenance",
    title: "Maintenance",
    breadcrumb: ["Mythic Tales", "Operations Support", "Maintenance"],
    contexts: ["Facility: Brewhouse A", "Crew: Day", "Mode: Critical first"],
    whatChanged: [
      "Centrifuge vibration alert raised severity to critical",
      "Spare pump inventory low"
    ],
    hero: [
      { label: "Equipment Uptime", value: "97.2%", detail: "Rolling 30 days" },
      { label: "Open Work Orders", value: "14", detail: "5 critical" },
      { label: "PM Compliance", value: "91%", detail: "Target ≥ 95%", trend: "▼ 2%" }
    ],
    queue: [
      {
        title: "Work order · Centrifuge vibration",
        status: "Critical",
        meta: "Assign senior tech · ETA ASAP",
        severity: "alert"
      },
      {
        title: "PM · Packaging line lubrication",
        status: "Due Today",
        meta: "Task owner: Lee",
        severity: "warning"
      },
      {
        title: "Parts request · Pump seals",
        status: "Waiting parts",
        meta: "On order · ETA 2 days",
        severity: "default"
      }
    ],
    activity: [
      {
        time: "07:58",
        summary: "Technician Kim completed CIP skids inspection",
        badge: "Complete"
      },
      { time: "07:30", summary: "Logged downtime 12 min · Can filler jam", badge: "Downtime" },
      { time: "06:55", summary: "Condition monitoring alert · Glycol chiller", badge: "Sensor" }
    ],
    quickActions: ["Create Work Order", "Request Part", "Log Downtime", "Open Equipment Map"],
    quickMeta: "Maintenance dispatch tools"
  },
  analytics: {
    key: "analytics",
    title: "Analytics",
    breadcrumb: ["Mythic Tales", "Insights", "Analytics"],
    contexts: ["Dashboard: Executive", "Interval: Weekly", "Compare: Last year"],
    whatChanged: [
      "Taproom performance index trending above forecast",
      "New predictive model ready for review"
    ],
    hero: [
      { label: "Net Revenue", value: "$4.8M", detail: "Week-to-date", trend: "▲ 6%" },
      { label: "Brew Efficiency", value: "92.4%", detail: "Goal 93%", trend: "▲ 0.5%" },
      { label: "Taproom Index", value: "108", detail: "Baseline 100", trend: "▲ 4" }
    ],
    queue: [
      {
        title: "Report delivery · Executive pack",
        status: "Scheduled",
        meta: "Send 17:00 · Email & Teams",
        severity: "default"
      },
      {
        title: "Anomaly investigation · CO2 usage",
        status: "Review",
        meta: "Assign analyst",
        severity: "warning"
      },
      {
        title: "Predictive model · Sales mix v2",
        status: "Ready",
        meta: "Requires business sign-off",
        severity: "default"
      }
    ],
    activity: [
      {
        time: "07:42",
        summary: 'Dashboard "Taproom pulse" updated with fresh data',
        badge: "Dashboard"
      },
      { time: "07:12", summary: "Alert dismissed · Keg loss variance normalized", badge: "Alert" },
      { time: "06:58", summary: "Forecast revised for July seasonal", badge: "Forecast" }
    ],
    quickActions: ["Share Report", "Annotate Metric", "Configure Alert", "Open Notebook"],
    quickMeta: "Analytics workspace shortcuts"
  },
  billing: {
    key: "billing",
    title: "Billing & Finance",
    breadcrumb: ["Mythic Tales", "Finance", "Billing"],
    contexts: ["Entity: Mythic Tales", "Aging: All", "View: At risk"],
    whatChanged: [
      "3 accounts moved to 60-day bucket",
      "Cash flow improved with $120k payment received"
    ],
    hero: [
      { label: "AR Aging", value: "$312k", detail: ">$60d: $48k", trend: "▼ $12k" },
      { label: "Cash Flow", value: "$188k", detail: "Week-to-date", trend: "▲ $32k" },
      { label: "Unapplied Payments", value: "$9.4k", detail: "5 payments to reconcile" }
    ],
    queue: [
      {
        title: "Invoice approval · INV-2041",
        status: "Needs review",
        meta: "Amount $18,200",
        severity: "warning"
      },
      {
        title: "Dispute · Barrel Bros",
        status: "Escalated",
        meta: "Open 12 days",
        severity: "alert"
      },
      {
        title: "Dunning · Hop City",
        status: "Due today",
        meta: "Send stage 2 notice",
        severity: "default"
      }
    ],
    activity: [
      { time: "07:50", summary: "Payment applied · Cascade Pub $12,400", badge: "Payment" },
      { time: "07:25", summary: "Credit memo created for Hoppy Trails", badge: "Credit" },
      { time: "06:48", summary: "Aging report exported to CSV", badge: "Report" }
    ],
    quickActions: ["Issue Invoice", "Apply Payment", "Escalate Dispute", "Open Aging Report"],
    quickMeta: "Finance desk shortcuts"
  },
  compliance: {
    key: "compliance",
    title: "Compliance & QA",
    breadcrumb: ["Mythic Tales", "Governance", "Compliance"],
    contexts: ["Program: Federal", "Audit: Spring", "View: Incidents"],
    whatChanged: [
      "Label approval pending for Citrus Wit",
      "CAPA task for cleaning protocol due today"
    ],
    hero: [
      { label: "Audit Readiness", value: "96%", detail: "Docs verified" },
      { label: "Permit Status", value: "All active", detail: "Next renewal 45 days" },
      { label: "Incidents (30d)", value: "2", detail: "0 critical" }
    ],
    queue: [
      {
        title: "File report · State excise",
        status: "Due 17:00",
        meta: "Requires finance review",
        severity: "warning"
      },
      {
        title: "CAPA · Cleaning SOP update",
        status: "In Progress",
        meta: "Owner: QA Lead",
        severity: "default"
      },
      {
        title: "Inspection prep checklist",
        status: "Ready",
        meta: "Brewery floor walkthrough",
        severity: "default"
      }
    ],
    activity: [
      { time: "07:32", summary: "Corrective action closed · Packaging deviation", badge: "CAPA" },
      { time: "07:05", summary: "Incident logged · Minor spill taproom", badge: "Incident" },
      { time: "06:49", summary: "Document updated · Allergen matrix", badge: "Docs" }
    ],
    quickActions: ["File Report", "Assign CAPA", "Download Certificates", "View Policies"],
    quickMeta: "Compliance center shortcuts"
  },
  iam: {
    key: "iam",
    title: "Identity & Access",
    breadcrumb: ["Mythic Tales", "Governance", "Identity & Access"],
    contexts: ["Org: Mythic Tales", "Review: Q2", "Mode: Risk"],
    whatChanged: ["8 access reviews due within 24 hours", "MFA adoption hit 92% goal"],
    hero: [
      { label: "Active Users", value: "318", detail: "12 contractors" },
      { label: "Pending Access Reviews", value: "8", detail: "Owners: Dept leads", trend: "▼ 3" },
      { label: "MFA Adoption", value: "92%", detail: "Goal 92%", trend: "▲ 1%" }
    ],
    queue: [
      {
        title: "Approve access · Taproom manager role",
        status: "Due Today",
        meta: "Request by Jamie Lee",
        severity: "warning"
      },
      {
        title: "Expiring permissions · Seasonal staff",
        status: "Review",
        meta: "7 accounts expire Friday",
        severity: "default"
      },
      {
        title: "Policy violation · Admin login anomaly",
        status: "Investigate",
        meta: "Triggered by IAM monitor",
        severity: "alert"
      }
    ],
    activity: [
      { time: "07:47", summary: "Revoked session · Suspicious IP in EU", badge: "Security" },
      {
        time: "07:16",
        summary: "Role change · Added Distribution Planner role to Erin",
        badge: "Role"
      },
      { time: "06:53", summary: "Completed access review · Finance managers", badge: "Review" }
    ],
    quickActions: ["Approve Access", "Generate Report", "Lock Account", "Open Audit Trail"],
    quickMeta: "Security ops shortcuts"
  }
};
var He = Object.defineProperty, Fe = Object.getOwnPropertyDescriptor, A = (i, e, t, a) => {
  for (var r = a > 1 ? void 0 : a ? Fe(e, t) : e, s = i.length - 1, o; s >= 0; s--)
    (o = i[s]) && (r = (a ? o(e, t, r) : o(r)) || r);
  return a && r && He(e, t, r), r;
};
let g = class extends O {
  constructor() {
    super(...arguments), this.domains = Le, this.selectedDomain = K, this.density = "compact", this.drawerOpen = !1, this.commandOpen = !1, this.toasts = [], this.toastId = 0, this.handleKeydown = (i) => {
      (i.metaKey || i.ctrlKey) && i.key.toLowerCase() === "k" && (i.preventDefault(), this.toggleCommandPalette()), i.key === "Escape" && (this.commandOpen && (i.preventDefault(), this.toggleCommandPalette(!1)), this.drawerOpen && (i.preventDefault(), this.closeDrawer()));
    };
  }
  connectedCallback() {
    super.connectedCallback(), window.addEventListener("keydown", this.handleKeydown), this.selectedDomain || (this.selectedDomain = K), this.setAttribute("data-density", this.density);
  }
  disconnectedCallback() {
    super.disconnectedCallback(), window.removeEventListener("keydown", this.handleKeydown);
  }
  updated() {
    this.setAttribute("data-density", this.density);
  }
  get activeDomain() {
    return this.domains[this.selectedDomain] ?? this.domains[K];
  }
  handleNavClick(i) {
    this.selectedDomain = i;
  }
  setDensity(i) {
    this.density = i;
  }
  toggleDrawer() {
    this.drawerOpen = !this.drawerOpen;
  }
  closeDrawer() {
    this.drawerOpen = !1;
  }
  toggleCommandPalette(i) {
    const e = i ?? !this.commandOpen;
    if (this.commandOpen = e, e)
      this.updateComplete.then(() => {
        const t = this.renderRoot.querySelector("#commandSearch");
        t == null || t.focus();
      });
    else {
      const t = this.renderRoot.querySelector("#commandPaletteTrigger");
      t == null || t.focus();
    }
  }
  showToast(i) {
    const e = ++this.toastId, t = { id: e, message: i };
    this.toasts = [...this.toasts, t], setTimeout(() => this.dismissToast(e), 2500);
  }
  dismissToast(i) {
    const e = this.renderRoot.querySelector(`#toast-${i}`);
    e && e.classList.add("toast--hide"), setTimeout(() => {
      this.toasts = this.toasts.filter((t) => t.id !== i);
    }, 300);
  }
  renderNav() {
    return ze.map(
      (i) => h`
        <div class="nav-group" aria-labelledby="${i.id}-group">
          <h3 id="${i.id}-group">${i.label}</h3>
          <ul>
            ${i.domains.map((e) => {
        const t = this.domains[e], a = this.selectedDomain === e;
        return h`
                <li>
                  <button
                    class=${k({ "nav-item": !0, active: a })}
                    data-domain=${e}
                    @click=${() => this.handleNavClick(e)}
                    aria-current=${a ? "page" : "false"}
                  >
                    ${t.title}
                  </button>
                </li>
              `;
      })}
          </ul>
        </div>
      `
    );
  }
  renderBreadcrumb() {
    const i = this.activeDomain, e = [...i.breadcrumb, i.title];
    return e.map((t, a) => {
      const r = a === e.length - 1;
      return h`
        <span class=${k({ breadcrumb: !0, current: r })}>${t}</span>
        ${r ? null : h`<span class="breadcrumb-separator">›</span>`}
      `;
    });
  }
  renderHeroCards() {
    return this.activeDomain.hero.map(
      (i) => h`
        <div class="hero-card">
          <span class="hero-card__label">${i.label}</span>
          <span class="hero-card__value">${i.value}</span>
          ${i.detail ? h`<span class="hero-card__detail">${i.detail}</span>` : null}
          ${i.trend ? h`<span class="hero-card__trend">${i.trend}</span>` : null}
        </div>
      `
    );
  }
  renderQueue() {
    return this.activeDomain.queue.map(
      (i) => h`
        <li class=${k({ "queue-item": !0, [`queue-item--${i.severity}`]: !0 })}>
          <div class="queue-item__title">${i.title}</div>
          <div class="queue-item__meta">${i.meta}</div>
          <span class="queue-item__status">${i.status}</span>
        </li>
      `
    );
  }
  renderActivity() {
    return this.activeDomain.activity.map(
      (i) => h`
        <li class="activity-item">
          <span class="activity-item__time">${i.time}</span>
          <div class="activity-item__summary">${i.summary}</div>
          <span class="activity-item__badge">${i.badge}</span>
        </li>
      `
    );
  }
  renderQuickActions() {
    return this.activeDomain.quickActions.map((i) => {
      const e = typeof i == "string" ? { label: i } : i, a = (e.label ?? "") || "Action", r = "message" in e && e.message ? e.message : `${a} queued`, s = "href" in e ? e.href : void 0, o = e.command;
      return h`
        <button
          class="quick-action"
          type="button"
          @click=${() => {
        if (o) {
          this.dispatchEvent(
            new CustomEvent("enterprise-command", {
              detail: {
                command: o,
                label: a,
                domain: this.selectedDomain
              },
              bubbles: !0,
              composed: !0
            })
          );
          return;
        }
        if (s) {
          window.location.href = s;
          return;
        }
        this.showToast(r);
      }}
        >
          ${a}
        </button>
      `;
    });
  }
  renderToasts() {
    return this.toasts.map(
      (i) => h` <div class="toast" id=${`toast-${i.id}`}>${i.message}</div> `
    );
  }
  render() {
    const i = [
      { id: "compact", label: "Compact" },
      { id: "comfortable", label: "Comfortable" }
    ];
    return h`
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
      (e) => h`<button class="chip" type="button">${e}</button>`
    )}
            </div>
            <div class="density-toggle" role="group" aria-label="Density toggle">
              <span>Density</span>
              ${i.map(
      ({ id: e, label: t }) => h`
                  <button
                    type="button"
                    class=${k({ "density-button": !0, active: this.density === e })}
                    data-density=${e}
                    @click=${() => this.setDensity(e)}
                  >
                    ${t}
                  </button>
                `
    )}
            </div>
          </div>

          <div class="what-changed" role="status" aria-live="polite">
            ${this.activeDomain.whatChanged.map(
      (e) => h`<span class="what-changed__item">${e}</span>`
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
              <div class="quick-actions__meta">${this.activeDomain.quickMeta ?? ""}</div>
            </header>
            <div class="quick-actions__grid">${this.renderQuickActions()}</div>
          </section>
        </main>

        <aside
          id="contextDrawer"
          class=${k({ "side-drawer": !0, "side-drawer--open": this.drawerOpen })}
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
          class=${k({ "command-overlay": !0, "command-overlay--open": this.commandOpen })}
          role="dialog"
          aria-modal="true"
          aria-labelledby="commandOverlayTitle"
          @click=${(e) => {
      e.target === e.currentTarget && this.toggleCommandPalette(!1);
    }}
        >
          <div class="command-panel">
            <header>
              <h2 id="commandOverlayTitle">Command Palette</h2>
              <button
                type="button"
                class="secondary"
                @click=${() => this.toggleCommandPalette(!1)}
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
};
g.styles = ge`
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
A([
  J({ type: Object })
], g.prototype, "domains", 2);
A([
  J({ type: String, attribute: "selected-domain" })
], g.prototype, "selectedDomain", 2);
A([
  L()
], g.prototype, "density", 2);
A([
  L()
], g.prototype, "drawerOpen", 2);
A([
  L()
], g.prototype, "commandOpen", 2);
A([
  L()
], g.prototype, "toasts", 2);
g = A([
  Re("mt-enterprise-console")
], g);
export {
  g as MtEnterpriseConsole,
  K as defaultDomain,
  ze as domainGroups,
  Le as enterpriseDomains
};
//# sourceMappingURL=mt-ui.es.js.map
