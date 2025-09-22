(function(u,b){typeof exports=="object"&&typeof module<"u"?b(exports):typeof define=="function"&&define.amd?define(["exports"],b):(u=typeof globalThis<"u"?globalThis:u||self,b(u.MythicTalesUI={}))})(this,function(u){"use strict";/**
 * @license
 * Copyright 2019 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */var ge;const b=globalThis,j=b.ShadowRoot&&(b.ShadyCSS===void 0||b.ShadyCSS.nativeShadow)&&"adoptedStyleSheets"in Document.prototype&&"replace"in CSSStyleSheet.prototype,F=Symbol(),Y=new WeakMap;let Z=class{constructor(e,t,i){if(this._$cssResult$=!0,i!==F)throw Error("CSSResult is not constructable. Use `unsafeCSS` or `css` instead.");this.cssText=e,this.t=t}get styleSheet(){let e=this.o;const t=this.t;if(j&&e===void 0){const i=t!==void 0&&t.length===1;i&&(e=Y.get(t)),e===void 0&&((this.o=e=new CSSStyleSheet).replaceSync(this.cssText),i&&Y.set(t,e))}return e}toString(){return this.cssText}};const be=r=>new Z(typeof r=="string"?r:r+"",void 0,F),ye=(r,...e)=>{const t=r.length===1?r[0]:e.reduce((i,a,s)=>i+(o=>{if(o._$cssResult$===!0)return o.cssText;if(typeof o=="number")return o;throw Error("Value passed to 'css' function must be a 'css' function result: "+o+". Use 'unsafeCSS' to pass non-literal values, but take care to ensure page security.")})(a)+r[s+1],r[0]);return new Z(t,r,F)},ve=(r,e)=>{if(j)r.adoptedStyleSheets=e.map(t=>t instanceof CSSStyleSheet?t:t.styleSheet);else for(const t of e){const i=document.createElement("style"),a=b.litNonce;a!==void 0&&i.setAttribute("nonce",a),i.textContent=t.cssText,r.appendChild(i)}},ee=j?r=>r:r=>r instanceof CSSStyleSheet?(e=>{let t="";for(const i of e.cssRules)t+=i.cssText;return be(t)})(r):r;/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const{is:fe,defineProperty:$e,getOwnPropertyDescriptor:we,getOwnPropertyNames:xe,getOwnPropertySymbols:_e,getPrototypeOf:Ae}=Object,y=globalThis,te=y.trustedTypes,ke=te?te.emptyScript:"",W=y.reactiveElementPolyfillSupport,T=(r,e)=>r,U={toAttribute(r,e){switch(e){case Boolean:r=r?ke:null;break;case Object:case Array:r=r==null?r:JSON.stringify(r)}return r},fromAttribute(r,e){let t=r;switch(e){case Boolean:t=r!==null;break;case Number:t=r===null?null:Number(r);break;case Object:case Array:try{t=JSON.parse(r)}catch{t=null}}return t}},K=(r,e)=>!fe(r,e),ie={attribute:!0,type:String,converter:U,reflect:!1,useDefault:!1,hasChanged:K};Symbol.metadata??(Symbol.metadata=Symbol("metadata")),y.litPropertyMetadata??(y.litPropertyMetadata=new WeakMap);let C=class extends HTMLElement{static addInitializer(e){this._$Ei(),(this.l??(this.l=[])).push(e)}static get observedAttributes(){return this.finalize(),this._$Eh&&[...this._$Eh.keys()]}static createProperty(e,t=ie){if(t.state&&(t.attribute=!1),this._$Ei(),this.prototype.hasOwnProperty(e)&&((t=Object.create(t)).wrapped=!0),this.elementProperties.set(e,t),!t.noAccessor){const i=Symbol(),a=this.getPropertyDescriptor(e,i,t);a!==void 0&&$e(this.prototype,e,a)}}static getPropertyDescriptor(e,t,i){const{get:a,set:s}=we(this.prototype,e)??{get(){return this[t]},set(o){this[t]=o}};return{get:a,set(o){const l=a==null?void 0:a.call(this);s==null||s.call(this,o),this.requestUpdate(e,l,i)},configurable:!0,enumerable:!0}}static getPropertyOptions(e){return this.elementProperties.get(e)??ie}static _$Ei(){if(this.hasOwnProperty(T("elementProperties")))return;const e=Ae(this);e.finalize(),e.l!==void 0&&(this.l=[...e.l]),this.elementProperties=new Map(e.elementProperties)}static finalize(){if(this.hasOwnProperty(T("finalized")))return;if(this.finalized=!0,this._$Ei(),this.hasOwnProperty(T("properties"))){const t=this.properties,i=[...xe(t),..._e(t)];for(const a of i)this.createProperty(a,t[a])}const e=this[Symbol.metadata];if(e!==null){const t=litPropertyMetadata.get(e);if(t!==void 0)for(const[i,a]of t)this.elementProperties.set(i,a)}this._$Eh=new Map;for(const[t,i]of this.elementProperties){const a=this._$Eu(t,i);a!==void 0&&this._$Eh.set(a,t)}this.elementStyles=this.finalizeStyles(this.styles)}static finalizeStyles(e){const t=[];if(Array.isArray(e)){const i=new Set(e.flat(1/0).reverse());for(const a of i)t.unshift(ee(a))}else e!==void 0&&t.push(ee(e));return t}static _$Eu(e,t){const i=t.attribute;return i===!1?void 0:typeof i=="string"?i:typeof e=="string"?e.toLowerCase():void 0}constructor(){super(),this._$Ep=void 0,this.isUpdatePending=!1,this.hasUpdated=!1,this._$Em=null,this._$Ev()}_$Ev(){var e;this._$ES=new Promise(t=>this.enableUpdating=t),this._$AL=new Map,this._$E_(),this.requestUpdate(),(e=this.constructor.l)==null||e.forEach(t=>t(this))}addController(e){var t;(this._$EO??(this._$EO=new Set)).add(e),this.renderRoot!==void 0&&this.isConnected&&((t=e.hostConnected)==null||t.call(e))}removeController(e){var t;(t=this._$EO)==null||t.delete(e)}_$E_(){const e=new Map,t=this.constructor.elementProperties;for(const i of t.keys())this.hasOwnProperty(i)&&(e.set(i,this[i]),delete this[i]);e.size>0&&(this._$Ep=e)}createRenderRoot(){const e=this.shadowRoot??this.attachShadow(this.constructor.shadowRootOptions);return ve(e,this.constructor.elementStyles),e}connectedCallback(){var e;this.renderRoot??(this.renderRoot=this.createRenderRoot()),this.enableUpdating(!0),(e=this._$EO)==null||e.forEach(t=>{var i;return(i=t.hostConnected)==null?void 0:i.call(t)})}enableUpdating(e){}disconnectedCallback(){var e;(e=this._$EO)==null||e.forEach(t=>{var i;return(i=t.hostDisconnected)==null?void 0:i.call(t)})}attributeChangedCallback(e,t,i){this._$AK(e,i)}_$ET(e,t){var s;const i=this.constructor.elementProperties.get(e),a=this.constructor._$Eu(e,i);if(a!==void 0&&i.reflect===!0){const o=(((s=i.converter)==null?void 0:s.toAttribute)!==void 0?i.converter:U).toAttribute(t,i.type);this._$Em=e,o==null?this.removeAttribute(a):this.setAttribute(a,o),this._$Em=null}}_$AK(e,t){var s,o;const i=this.constructor,a=i._$Eh.get(e);if(a!==void 0&&this._$Em!==a){const l=i.getPropertyOptions(a),n=typeof l.converter=="function"?{fromAttribute:l.converter}:((s=l.converter)==null?void 0:s.fromAttribute)!==void 0?l.converter:U;this._$Em=a;const c=n.fromAttribute(t,l.type);this[a]=c??((o=this._$Ej)==null?void 0:o.get(a))??c,this._$Em=null}}requestUpdate(e,t,i){var a;if(e!==void 0){const s=this.constructor,o=this[e];if(i??(i=s.getPropertyOptions(e)),!((i.hasChanged??K)(o,t)||i.useDefault&&i.reflect&&o===((a=this._$Ej)==null?void 0:a.get(e))&&!this.hasAttribute(s._$Eu(e,i))))return;this.C(e,t,i)}this.isUpdatePending===!1&&(this._$ES=this._$EP())}C(e,t,{useDefault:i,reflect:a,wrapped:s},o){i&&!(this._$Ej??(this._$Ej=new Map)).has(e)&&(this._$Ej.set(e,o??t??this[e]),s!==!0||o!==void 0)||(this._$AL.has(e)||(this.hasUpdated||i||(t=void 0),this._$AL.set(e,t)),a===!0&&this._$Em!==e&&(this._$Eq??(this._$Eq=new Set)).add(e))}async _$EP(){this.isUpdatePending=!0;try{await this._$ES}catch(t){Promise.reject(t)}const e=this.scheduleUpdate();return e!=null&&await e,!this.isUpdatePending}scheduleUpdate(){return this.performUpdate()}performUpdate(){var i;if(!this.isUpdatePending)return;if(!this.hasUpdated){if(this.renderRoot??(this.renderRoot=this.createRenderRoot()),this._$Ep){for(const[s,o]of this._$Ep)this[s]=o;this._$Ep=void 0}const a=this.constructor.elementProperties;if(a.size>0)for(const[s,o]of a){const{wrapped:l}=o,n=this[s];l!==!0||this._$AL.has(s)||n===void 0||this.C(s,void 0,o,n)}}let e=!1;const t=this._$AL;try{e=this.shouldUpdate(t),e?(this.willUpdate(t),(i=this._$EO)==null||i.forEach(a=>{var s;return(s=a.hostUpdate)==null?void 0:s.call(a)}),this.update(t)):this._$EM()}catch(a){throw e=!1,this._$EM(),a}e&&this._$AE(t)}willUpdate(e){}_$AE(e){var t;(t=this._$EO)==null||t.forEach(i=>{var a;return(a=i.hostUpdated)==null?void 0:a.call(i)}),this.hasUpdated||(this.hasUpdated=!0,this.firstUpdated(e)),this.updated(e)}_$EM(){this._$AL=new Map,this.isUpdatePending=!1}get updateComplete(){return this.getUpdateComplete()}getUpdateComplete(){return this._$ES}shouldUpdate(e){return!0}update(e){this._$Eq&&(this._$Eq=this._$Eq.forEach(t=>this._$ET(t,this[t]))),this._$EM()}updated(e){}firstUpdated(e){}};C.elementStyles=[],C.shadowRootOptions={mode:"open"},C[T("elementProperties")]=new Map,C[T("finalized")]=new Map,W==null||W({ReactiveElement:C}),(y.reactiveElementVersions??(y.reactiveElementVersions=[])).push("2.1.1");/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const E=globalThis,N=E.trustedTypes,ae=N?N.createPolicy("lit-html",{createHTML:r=>r}):void 0,re="$lit$",v=`lit$${Math.random().toFixed(9).slice(2)}$`,se="?"+v,Ce=`<${se}>`,$=document,M=()=>$.createComment(""),O=r=>r===null||typeof r!="object"&&typeof r!="function",Q=Array.isArray,Se=r=>Q(r)||typeof(r==null?void 0:r[Symbol.iterator])=="function",V=`[ 	
\f\r]`,D=/<(?:(!--|\/[^a-zA-Z])|(\/?[a-zA-Z][^>\s]*)|(\/?$))/g,oe=/-->/g,ne=/>/g,w=RegExp(`>|${V}(?:([^\\s"'>=/]+)(${V}*=${V}*(?:[^ 	
\f\r"'\`<>=]|("|')|))|$)`,"g"),le=/'/g,de=/"/g,ce=/^(?:script|style|textarea|title)$/i,Pe=r=>(e,...t)=>({_$litType$:r,strings:e,values:t}),m=Pe(1),x=Symbol.for("lit-noChange"),p=Symbol.for("lit-nothing"),ue=new WeakMap,_=$.createTreeWalker($,129);function pe(r,e){if(!Q(r)||!r.hasOwnProperty("raw"))throw Error("invalid template strings array");return ae!==void 0?ae.createHTML(e):e}const Te=(r,e)=>{const t=r.length-1,i=[];let a,s=e===2?"<svg>":e===3?"<math>":"",o=D;for(let l=0;l<t;l++){const n=r[l];let c,h,d=-1,g=0;for(;g<n.length&&(o.lastIndex=g,h=o.exec(n),h!==null);)g=o.lastIndex,o===D?h[1]==="!--"?o=oe:h[1]!==void 0?o=ne:h[2]!==void 0?(ce.test(h[2])&&(a=RegExp("</"+h[2],"g")),o=w):h[3]!==void 0&&(o=w):o===w?h[0]===">"?(o=a??D,d=-1):h[1]===void 0?d=-2:(d=o.lastIndex-h[2].length,c=h[1],o=h[3]===void 0?w:h[3]==='"'?de:le):o===de||o===le?o=w:o===oe||o===ne?o=D:(o=w,a=void 0);const f=o===w&&r[l+1].startsWith("/>")?" ":"";s+=o===D?n+Ce:d>=0?(i.push(c),n.slice(0,d)+re+n.slice(d)+v+f):n+v+(d===-2?l:f)}return[pe(r,s+(r[t]||"<?>")+(e===2?"</svg>":e===3?"</math>":"")),i]};class R{constructor({strings:e,_$litType$:t},i){let a;this.parts=[];let s=0,o=0;const l=e.length-1,n=this.parts,[c,h]=Te(e,t);if(this.el=R.createElement(c,i),_.currentNode=this.el.content,t===2||t===3){const d=this.el.content.firstChild;d.replaceWith(...d.childNodes)}for(;(a=_.nextNode())!==null&&n.length<l;){if(a.nodeType===1){if(a.hasAttributes())for(const d of a.getAttributeNames())if(d.endsWith(re)){const g=h[o++],f=a.getAttribute(d).split(v),H=/([.?@])?(.*)/.exec(g);n.push({type:1,index:s,name:H[2],strings:f,ctor:H[1]==="."?Me:H[1]==="?"?Oe:H[1]==="@"?De:B}),a.removeAttribute(d)}else d.startsWith(v)&&(n.push({type:6,index:s}),a.removeAttribute(d));if(ce.test(a.tagName)){const d=a.textContent.split(v),g=d.length-1;if(g>0){a.textContent=N?N.emptyScript:"";for(let f=0;f<g;f++)a.append(d[f],M()),_.nextNode(),n.push({type:2,index:++s});a.append(d[g],M())}}}else if(a.nodeType===8)if(a.data===se)n.push({type:2,index:s});else{let d=-1;for(;(d=a.data.indexOf(v,d+1))!==-1;)n.push({type:7,index:s}),d+=v.length-1}s++}}static createElement(e,t){const i=$.createElement("template");return i.innerHTML=e,i}}function S(r,e,t=r,i){var o,l;if(e===x)return e;let a=i!==void 0?(o=t._$Co)==null?void 0:o[i]:t._$Cl;const s=O(e)?void 0:e._$litDirective$;return(a==null?void 0:a.constructor)!==s&&((l=a==null?void 0:a._$AO)==null||l.call(a,!1),s===void 0?a=void 0:(a=new s(r),a._$AT(r,t,i)),i!==void 0?(t._$Co??(t._$Co=[]))[i]=a:t._$Cl=a),a!==void 0&&(e=S(r,a._$AS(r,e.values),a,i)),e}class Ee{constructor(e,t){this._$AV=[],this._$AN=void 0,this._$AD=e,this._$AM=t}get parentNode(){return this._$AM.parentNode}get _$AU(){return this._$AM._$AU}u(e){const{el:{content:t},parts:i}=this._$AD,a=((e==null?void 0:e.creationScope)??$).importNode(t,!0);_.currentNode=a;let s=_.nextNode(),o=0,l=0,n=i[0];for(;n!==void 0;){if(o===n.index){let c;n.type===2?c=new q(s,s.nextSibling,this,e):n.type===1?c=new n.ctor(s,n.name,n.strings,this,e):n.type===6&&(c=new Re(s,this,e)),this._$AV.push(c),n=i[++l]}o!==(n==null?void 0:n.index)&&(s=_.nextNode(),o++)}return _.currentNode=$,a}p(e){let t=0;for(const i of this._$AV)i!==void 0&&(i.strings!==void 0?(i._$AI(e,i,t),t+=i.strings.length-2):i._$AI(e[t])),t++}}class q{get _$AU(){var e;return((e=this._$AM)==null?void 0:e._$AU)??this._$Cv}constructor(e,t,i,a){this.type=2,this._$AH=p,this._$AN=void 0,this._$AA=e,this._$AB=t,this._$AM=i,this.options=a,this._$Cv=(a==null?void 0:a.isConnected)??!0}get parentNode(){let e=this._$AA.parentNode;const t=this._$AM;return t!==void 0&&(e==null?void 0:e.nodeType)===11&&(e=t.parentNode),e}get startNode(){return this._$AA}get endNode(){return this._$AB}_$AI(e,t=this){e=S(this,e,t),O(e)?e===p||e==null||e===""?(this._$AH!==p&&this._$AR(),this._$AH=p):e!==this._$AH&&e!==x&&this._(e):e._$litType$!==void 0?this.$(e):e.nodeType!==void 0?this.T(e):Se(e)?this.k(e):this._(e)}O(e){return this._$AA.parentNode.insertBefore(e,this._$AB)}T(e){this._$AH!==e&&(this._$AR(),this._$AH=this.O(e))}_(e){this._$AH!==p&&O(this._$AH)?this._$AA.nextSibling.data=e:this.T($.createTextNode(e)),this._$AH=e}$(e){var s;const{values:t,_$litType$:i}=e,a=typeof i=="number"?this._$AC(e):(i.el===void 0&&(i.el=R.createElement(pe(i.h,i.h[0]),this.options)),i);if(((s=this._$AH)==null?void 0:s._$AD)===a)this._$AH.p(t);else{const o=new Ee(a,this),l=o.u(this.options);o.p(t),this.T(l),this._$AH=o}}_$AC(e){let t=ue.get(e.strings);return t===void 0&&ue.set(e.strings,t=new R(e)),t}k(e){Q(this._$AH)||(this._$AH=[],this._$AR());const t=this._$AH;let i,a=0;for(const s of e)a===t.length?t.push(i=new q(this.O(M()),this.O(M()),this,this.options)):i=t[a],i._$AI(s),a++;a<t.length&&(this._$AR(i&&i._$AB.nextSibling,a),t.length=a)}_$AR(e=this._$AA.nextSibling,t){var i;for((i=this._$AP)==null?void 0:i.call(this,!1,!0,t);e!==this._$AB;){const a=e.nextSibling;e.remove(),e=a}}setConnected(e){var t;this._$AM===void 0&&(this._$Cv=e,(t=this._$AP)==null||t.call(this,e))}}class B{get tagName(){return this.element.tagName}get _$AU(){return this._$AM._$AU}constructor(e,t,i,a,s){this.type=1,this._$AH=p,this._$AN=void 0,this.element=e,this.name=t,this._$AM=a,this.options=s,i.length>2||i[0]!==""||i[1]!==""?(this._$AH=Array(i.length-1).fill(new String),this.strings=i):this._$AH=p}_$AI(e,t=this,i,a){const s=this.strings;let o=!1;if(s===void 0)e=S(this,e,t,0),o=!O(e)||e!==this._$AH&&e!==x,o&&(this._$AH=e);else{const l=e;let n,c;for(e=s[0],n=0;n<s.length-1;n++)c=S(this,l[i+n],t,n),c===x&&(c=this._$AH[n]),o||(o=!O(c)||c!==this._$AH[n]),c===p?e=p:e!==p&&(e+=(c??"")+s[n+1]),this._$AH[n]=c}o&&!a&&this.j(e)}j(e){e===p?this.element.removeAttribute(this.name):this.element.setAttribute(this.name,e??"")}}class Me extends B{constructor(){super(...arguments),this.type=3}j(e){this.element[this.name]=e===p?void 0:e}}class Oe extends B{constructor(){super(...arguments),this.type=4}j(e){this.element.toggleAttribute(this.name,!!e&&e!==p)}}class De extends B{constructor(e,t,i,a,s){super(e,t,i,a,s),this.type=5}_$AI(e,t=this){if((e=S(this,e,t,0)??p)===x)return;const i=this._$AH,a=e===p&&i!==p||e.capture!==i.capture||e.once!==i.once||e.passive!==i.passive,s=e!==p&&(i===p||a);a&&this.element.removeEventListener(this.name,this,i),s&&this.element.addEventListener(this.name,this,e),this._$AH=e}handleEvent(e){var t;typeof this._$AH=="function"?this._$AH.call(((t=this.options)==null?void 0:t.host)??this.element,e):this._$AH.handleEvent(e)}}class Re{constructor(e,t,i){this.element=e,this.type=6,this._$AN=void 0,this._$AM=t,this.options=i}get _$AU(){return this._$AM._$AU}_$AI(e){S(this,e)}}const G=E.litHtmlPolyfillSupport;G==null||G(R,q),(E.litHtmlVersions??(E.litHtmlVersions=[])).push("3.3.1");const qe=(r,e,t)=>{const i=(t==null?void 0:t.renderBefore)??e;let a=i._$litPart$;if(a===void 0){const s=(t==null?void 0:t.renderBefore)??null;i._$litPart$=a=new q(e.insertBefore(M(),s),s,void 0,t??{})}return a._$AI(r),a};/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const A=globalThis;let I=class extends C{constructor(){super(...arguments),this.renderOptions={host:this},this._$Do=void 0}createRenderRoot(){var t;const e=super.createRenderRoot();return(t=this.renderOptions).renderBefore??(t.renderBefore=e.firstChild),e}update(e){const t=this.render();this.hasUpdated||(this.renderOptions.isConnected=this.isConnected),super.update(e),this._$Do=qe(t,this.renderRoot,this.renderOptions)}connectedCallback(){var e;super.connectedCallback(),(e=this._$Do)==null||e.setConnected(!0)}disconnectedCallback(){var e;super.disconnectedCallback(),(e=this._$Do)==null||e.setConnected(!1)}render(){return x}};I._$litElement$=!0,I.finalized=!0,(ge=A.litElementHydrateSupport)==null||ge.call(A,{LitElement:I});const X=A.litElementPolyfillSupport;X==null||X({LitElement:I}),(A.litElementVersions??(A.litElementVersions=[])).push("4.2.1");/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const Ie=r=>(e,t)=>{t!==void 0?t.addInitializer(()=>{customElements.define(r,e)}):customElements.define(r,e)};/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const Ue={attribute:!0,type:String,converter:U,reflect:!1,hasChanged:K},Ne=(r=Ue,e,t)=>{const{kind:i,metadata:a}=t;let s=globalThis.litPropertyMetadata.get(a);if(s===void 0&&globalThis.litPropertyMetadata.set(a,s=new Map),i==="setter"&&((r=Object.create(r)).wrapped=!0),s.set(t.name,r),i==="accessor"){const{name:o}=t;return{set(l){const n=e.get.call(this);e.set.call(this,l),this.requestUpdate(o,n,r)},init(l){return l!==void 0&&this.C(o,void 0,r,l),l}}}if(i==="setter"){const{name:o}=t;return function(l){const n=this[o];e.call(this,l),this.requestUpdate(o,n,r)}}throw Error("Unsupported decorator location: "+i)};function J(r){return(e,t)=>typeof t=="object"?Ne(r,e,t):((i,a,s)=>{const o=a.hasOwnProperty(s);return a.constructor.createProperty(s,i),o?Object.getOwnPropertyDescriptor(a,s):void 0})(r,e,t)}/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */function z(r){return J({...r,state:!0,attribute:!1})}/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const Be={ATTRIBUTE:1},ze=r=>(...e)=>({_$litDirective$:r,values:e});class Le{constructor(e){}get _$AU(){return this._$AM._$AU}_$AT(e,t,i){this._$Ct=e,this._$AM=t,this._$Ci=i}_$AS(e,t){return this.update(e,t)}update(e,t){return this.render(...t)}}/**
 * @license
 * Copyright 2018 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const P=ze(class extends Le{constructor(r){var e;if(super(r),r.type!==Be.ATTRIBUTE||r.name!=="class"||((e=r.strings)==null?void 0:e.length)>2)throw Error("`classMap()` can only be used in the `class` attribute and must be the only part in the attribute.")}render(r){return" "+Object.keys(r).filter(e=>r[e]).join(" ")+" "}update(r,[e]){var i,a;if(this.st===void 0){this.st=new Set,r.strings!==void 0&&(this.nt=new Set(r.strings.join(" ").split(/\s/).filter(s=>s!=="")));for(const s in e)e[s]&&!((i=this.nt)!=null&&i.has(s))&&this.st.add(s);return this.render(e)}const t=r.element.classList;for(const s of this.st)s in e||(t.remove(s),this.st.delete(s));for(const s in e){const o=!!e[s];o===this.st.has(s)||(a=this.nt)!=null&&a.has(s)||(o?(t.add(s),this.st.add(s)):(t.remove(s),this.st.delete(s)))}return x}}),he=[{id:"operations",label:"Operations",domains:["production","prodinventory","keginventory","taproom"]},{id:"insights",label:"Insights & Governance",domains:["iam"]}],L="production",me={production:{key:"production",title:"Production",breadcrumb:["Mythic Tales","Brewery Ops","Production"],contexts:["Brewery: Mythic Central","Facility: Brewhouse A","Shift: Day"],whatChanged:["CIP for Fermenter 4 overdue by 8 hours","Cooling loop maintenance scheduled for tonight"],hero:[{label:"Batches Active",value:"4",detail:"2 conditioning",trend:"▲ 1 vs yesterday"},{label:"Fermenter Capacity",value:"68%",detail:"6 of 18 vessels free"},{label:"Upcoming Brews",value:"3",detail:"Next: Hazy IPA 09:00"}],queue:[{title:"Batch #MT-87 · Mash In",status:"In Progress",meta:"Started 08:15 · Mash tun 2",severity:"default"},{title:"Batch #MT-86 · Transfer to FV4",status:"Blocked",meta:"Awaiting QC sign-off",severity:"alert"},{title:"Dry hop preparation",status:"Ready",meta:"Galaxy / Mosaic · 11:30",severity:"warning"}],activity:[{time:"07:45",summary:"QA sample for Batch #MT-85 passed dissolved oxygen check",badge:"QA"},{time:"07:10",summary:"Boil kettle CIP completed",badge:"Maintenance"},{time:"06:50",summary:"Operator note: Pump cavitation resolved on line B",badge:"Ops"}],quickActions:["Start Batch","Log Deviation","Schedule CIP","Assign Operator"],quickMeta:"Favorite shortcuts based on last 30 days"},prodinventory:{key:"prodinventory",title:"Production Inventory",breadcrumb:["Mythic Tales","Inventory","Production"],contexts:["Brewery: Mythic Central","Warehouse: Raw A","View: Reorder risk"],whatChanged:["Malt (Pilsner) dropped below reorder point by 2 pallets","Lot #LA-204 pending lab release"],hero:[{label:"Stock vs Reorder",value:"82%",detail:"5 items at risk",trend:"▼ 6% week over week"},{label:"WIP Lots",value:"12",detail:"4 awaiting QA"},{label:"Shrinkage Rate",value:"1.6%",detail:"Target 1.2%",trend:"▲ 0.3%"}],queue:[{title:"Receive Malt Delivery · PO #4521",status:"Due 10:00",meta:"Dock 2 · Requires tare weights",severity:"default"},{title:"Lot release · Yeast Prop 14",status:"Waiting QA",meta:"Lab review ETA 12:00",severity:"warning"},{title:"Cycle count · Hops Freezer",status:"Blocked",meta:"Freezer under maintenance",severity:"alert"}],activity:[{time:"07:55",summary:"Issued 400kg Pilsner malt to Batch #MT-87",badge:"Issue"},{time:"07:20",summary:"Adjusted lot #CARA-33 by -5kg (spillage)",badge:"Adjustment"},{time:"06:45",summary:"Created transfer ticket for CO2 cylinders",badge:"Transfer"}],quickActions:["Create Transfer","Adjust Inventory","Print Labels","Schedule Count"],quickMeta:"Inventory coordinators · Top actions"},keginventory:{key:"keginventory",title:"Keg Inventory",breadcrumb:["Mythic Tales","Logistics","Keg Inventory"],contexts:["Region: Northwest","Depot: Portland","View: Turns"],whatChanged:["12 kegs overdue for maintenance","Return shipment MT-RT-219 arrived 06:30"],hero:[{label:"Kegs Available",value:"164",detail:"96×1/2 bbl · 68×1/6 bbl"},{label:"Turn Time",value:"9.2 days",detail:"Target ≤ 8 days",trend:"▲ 0.7d"},{label:"Maintenance Overdue",value:"8%",detail:"12 of 150 in rotation",trend:"▲ 2%"}],queue:[{title:"Prep order · Taproom Downtown",status:"Due 11:00",meta:"12 kegs · Load route PDX-3",severity:"default"},{title:"Maintenance · Valve replacement",status:"In Progress",meta:"Keg IDs 7721-7726",severity:"warning"},{title:"Return processing · Distributor NW",status:"Ready",meta:"Scan and sanitize 36 kegs",severity:"default"}],activity:[{time:"07:40",summary:"Scanned keg #7718 → Assigned to Route PDX-2",badge:"Scan"},{time:"07:15",summary:"Marked keg #5521 as needs maintenance",badge:"Maintenance"},{time:"06:55",summary:"Completed tap swap · Taproom Pearl District",badge:"Taproom"}],quickActions:["Scan Keg","Assign Route","Mark Maintenance","View Map"],quickMeta:"Logistics shortcuts"},catalog:{key:"catalog",title:"Catalog",breadcrumb:["Mythic Tales","Commercial","Catalog"],contexts:["Season: Spring","Portfolio: Core","Compliance: On track"],whatChanged:["Seasonal label artwork awaiting compliance review","IPA SKU cost updated with new hop contract"],hero:[{label:"Active SKUs",value:"48",detail:"6 seasonal",trend:"▲ 2 new"},{label:"Seasonal Readiness",value:"83%",detail:"Artwork 1 pending"},{label:"Compliance Checklist",value:"92%",detail:"2 items require update"}],queue:[{title:"Recipe approval · MT-IPA-2024",status:"Review",meta:"Needs product manager sign-off",severity:"warning"},{title:"Artwork review · Hazy Galaxy",status:"In Progress",meta:"Compliance check scheduled 13:00",severity:"default"},{title:"Distribution eligibility · Nitro Stout",status:"Blocked",meta:"Awaiting keg availability confirmation",severity:"alert"}],activity:[{time:"07:50",summary:"Updated label spec for Golden Lager",badge:"Label"},{time:"07:05",summary:"Costing refreshed for Pumpkin Ale",badge:"Costing"},{time:"06:40",summary:"New SKU draft created: Citrus Wit",badge:"SKU"}],quickActions:["Create SKU","Duplicate Recipe","Submit for Compliance","Open Asset Library"],quickMeta:"Commercial toolkit"},taproom:{key:"taproom",title:"Taproom Operations",breadcrumb:["Mythic Tales","Customer Experience","Taproom Operations"],contexts:["Taproom: Pearl District","Shift: Morning","Mode: Service"],whatChanged:["Keg blow risk on Tap #7 within 45 minutes","Event prep checklist due by 15:00"],hero:[{label:"Sales Velocity",value:"$2.3k",detail:"Last 4 hrs",trend:"▲ 9%"},{label:"Keg Blow Risk",value:"3 taps",detail:"Monitor taps 7, 11, 12"},{label:"Staff Coverage",value:"100%",detail:"6 scheduled · 6 present"}],queue:[{title:"Ticket #482 · Table 14",status:"Needs Response",meta:"Guest waiting 3 min",severity:"warning"},{title:"Event prep · Trivia Night",status:"Due 15:00",meta:"Setup AV + reserved tables",severity:"default"},{title:"Keg changeover · Tap #11",status:"In Progress",meta:"Assign to Alex",severity:"default"}],activity:[{time:"07:55",summary:"Ticket #478 resolved · Order comped",badge:"Service"},{time:"07:25",summary:"Logged pour anomaly on tap #4",badge:"Analytics"},{time:"06:58",summary:"Shift notes updated by Lead Sam",badge:"Shift"}],quickActions:["Open Ticket","Trigger Keg Swap","Update Taplist","Broadcast Shift Note"],quickMeta:"Taproom lead shortcuts"},sales:{key:"sales",title:"Sales",breadcrumb:["Mythic Tales","Revenue","Sales"],contexts:["Region: West","Segment: On-premise","Quarter: Q2"],whatChanged:["Three offers expiring in 48 hours","Distributor UrbanCraft flagged credit risk"],hero:[{label:"Bookings vs Target",value:"92%",detail:"Target $1.2M · Booked $1.1M",trend:"▲ 4% WoW"},{label:"Open Opportunities",value:"18",detail:"6 in commit"},{label:"Fulfillment Status",value:"88%",detail:"4 orders need confirmation"}],queue:[{title:"Quote #Q-2091 · Copper Tap",status:"Expires 24h",meta:"Need pricing approval",severity:"warning"},{title:"Delivery confirmation · Hoppy Trails",status:"Awaiting POD",meta:"Driver ETA 10:30",severity:"default"},{title:"Opportunity · Riverfront Bistro",status:"Next step: tasting",meta:"Schedule by Friday",severity:"default"}],activity:[{time:"07:48",summary:"Logged call with Barrel House",badge:"Call"},{time:"07:18",summary:"Order #SO-1187 updated shipping address",badge:"Order"},{time:"06:55",summary:"Credit hold added for UrbanCraft Distributing",badge:"Finance"}],quickActions:["Create Order","Log Interaction","Schedule Tasting","Generate Quote"],quickMeta:"Sales ops favorites"},distribution:{key:"distribution",title:"Distribution & Logistics",breadcrumb:["Mythic Tales","Logistics","Distribution"],contexts:["Hub: Portland","Fleet: Night Shift","View: Exceptions"],whatChanged:["Route PDX-5 flagged temperature deviation","Carrier SwiftCrate escalated delayed pickup"],hero:[{label:"Fleet Utilization",value:"76%",detail:"9 of 12 vehicles dispatched"},{label:"On-time Delivery",value:"94%",detail:"Goal ≥ 96%",trend:"▼ 1%"},{label:"Route Exceptions",value:"3",detail:"Temperature, delay, reroute"}],queue:[{title:"Dispatch · Route PDX-3",status:"Loading",meta:"Depart 09:30 · Driver Kim",severity:"default"},{title:"Optimize route · Seattle loop",status:"Action needed",meta:"Add new stop Hoppy Coop",severity:"warning"},{title:"Carrier escalation · SwiftCrate",status:"High",meta:"Escalate to manager",severity:"alert"}],activity:[{time:"07:52",summary:"Proof of delivery uploaded for Route PDX-1",badge:"POD"},{time:"07:33",summary:"Route PDX-5 temperature alert acknowledged",badge:"Alert"},{time:"06:59",summary:"Driver note: Traffic congestion on I-5",badge:"Driver"}],quickActions:["Assign Route","Notify Carrier","Print Manifests","Open Map View"],quickMeta:"Dispatch center tools"},procurement:{key:"procurement",title:"Procurement",breadcrumb:["Mythic Tales","Supply","Procurement"],contexts:["Category: Packaging","Budget: FY24","View: Expiring contracts"],whatChanged:["Supplier BrewPack OTIF dipped to 88%","Contract with HopWorld expires in 30 days"],hero:[{label:"Spend vs Budget",value:"61%",detail:"YTD"},{label:"Supplier OTIF",value:"93%",detail:"Target ≥ 95%",trend:"▼ 1%"},{label:"Contracts Renewing",value:"5",detail:"Next 45 days"}],queue:[{title:"RFQ · Crowler lids",status:"Responses due 17:00",meta:"3 suppliers invited",severity:"warning"},{title:"PO approval · #PO-5524",status:"Awaiting finance",meta:"Total $18,400",severity:"default"},{title:"Contract renewal · HopWorld",status:"Plan review",meta:"Review pricing tiers",severity:"default"}],activity:[{time:"07:40",summary:"Added supplier note for YeastWorks",badge:"Supplier"},{time:"07:18",summary:"Invoice mismatch flagged · Packaging tape",badge:"Invoice"},{time:"06:47",summary:"RFQ launched for glass growlers",badge:"RFQ"}],quickActions:["Create PO","Launch RFQ","Record Supplier Note","View Contracts"],quickMeta:"Strategic sourcing quick links"},maintenance:{key:"maintenance",title:"Maintenance",breadcrumb:["Mythic Tales","Operations Support","Maintenance"],contexts:["Facility: Brewhouse A","Crew: Day","Mode: Critical first"],whatChanged:["Centrifuge vibration alert raised severity to critical","Spare pump inventory low"],hero:[{label:"Equipment Uptime",value:"97.2%",detail:"Rolling 30 days"},{label:"Open Work Orders",value:"14",detail:"5 critical"},{label:"PM Compliance",value:"91%",detail:"Target ≥ 95%",trend:"▼ 2%"}],queue:[{title:"Work order · Centrifuge vibration",status:"Critical",meta:"Assign senior tech · ETA ASAP",severity:"alert"},{title:"PM · Packaging line lubrication",status:"Due Today",meta:"Task owner: Lee",severity:"warning"},{title:"Parts request · Pump seals",status:"Waiting parts",meta:"On order · ETA 2 days",severity:"default"}],activity:[{time:"07:58",summary:"Technician Kim completed CIP skids inspection",badge:"Complete"},{time:"07:30",summary:"Logged downtime 12 min · Can filler jam",badge:"Downtime"},{time:"06:55",summary:"Condition monitoring alert · Glycol chiller",badge:"Sensor"}],quickActions:["Create Work Order","Request Part","Log Downtime","Open Equipment Map"],quickMeta:"Maintenance dispatch tools"},analytics:{key:"analytics",title:"Analytics",breadcrumb:["Mythic Tales","Insights","Analytics"],contexts:["Dashboard: Executive","Interval: Weekly","Compare: Last year"],whatChanged:["Taproom performance index trending above forecast","New predictive model ready for review"],hero:[{label:"Net Revenue",value:"$4.8M",detail:"Week-to-date",trend:"▲ 6%"},{label:"Brew Efficiency",value:"92.4%",detail:"Goal 93%",trend:"▲ 0.5%"},{label:"Taproom Index",value:"108",detail:"Baseline 100",trend:"▲ 4"}],queue:[{title:"Report delivery · Executive pack",status:"Scheduled",meta:"Send 17:00 · Email & Teams",severity:"default"},{title:"Anomaly investigation · CO2 usage",status:"Review",meta:"Assign analyst",severity:"warning"},{title:"Predictive model · Sales mix v2",status:"Ready",meta:"Requires business sign-off",severity:"default"}],activity:[{time:"07:42",summary:'Dashboard "Taproom pulse" updated with fresh data',badge:"Dashboard"},{time:"07:12",summary:"Alert dismissed · Keg loss variance normalized",badge:"Alert"},{time:"06:58",summary:"Forecast revised for July seasonal",badge:"Forecast"}],quickActions:["Share Report","Annotate Metric","Configure Alert","Open Notebook"],quickMeta:"Analytics workspace shortcuts"},billing:{key:"billing",title:"Billing & Finance",breadcrumb:["Mythic Tales","Finance","Billing"],contexts:["Entity: Mythic Tales","Aging: All","View: At risk"],whatChanged:["3 accounts moved to 60-day bucket","Cash flow improved with $120k payment received"],hero:[{label:"AR Aging",value:"$312k",detail:">$60d: $48k",trend:"▼ $12k"},{label:"Cash Flow",value:"$188k",detail:"Week-to-date",trend:"▲ $32k"},{label:"Unapplied Payments",value:"$9.4k",detail:"5 payments to reconcile"}],queue:[{title:"Invoice approval · INV-2041",status:"Needs review",meta:"Amount $18,200",severity:"warning"},{title:"Dispute · Barrel Bros",status:"Escalated",meta:"Open 12 days",severity:"alert"},{title:"Dunning · Hop City",status:"Due today",meta:"Send stage 2 notice",severity:"default"}],activity:[{time:"07:50",summary:"Payment applied · Cascade Pub $12,400",badge:"Payment"},{time:"07:25",summary:"Credit memo created for Hoppy Trails",badge:"Credit"},{time:"06:48",summary:"Aging report exported to CSV",badge:"Report"}],quickActions:["Issue Invoice","Apply Payment","Escalate Dispute","Open Aging Report"],quickMeta:"Finance desk shortcuts"},compliance:{key:"compliance",title:"Compliance & QA",breadcrumb:["Mythic Tales","Governance","Compliance"],contexts:["Program: Federal","Audit: Spring","View: Incidents"],whatChanged:["Label approval pending for Citrus Wit","CAPA task for cleaning protocol due today"],hero:[{label:"Audit Readiness",value:"96%",detail:"Docs verified"},{label:"Permit Status",value:"All active",detail:"Next renewal 45 days"},{label:"Incidents (30d)",value:"2",detail:"0 critical"}],queue:[{title:"File report · State excise",status:"Due 17:00",meta:"Requires finance review",severity:"warning"},{title:"CAPA · Cleaning SOP update",status:"In Progress",meta:"Owner: QA Lead",severity:"default"},{title:"Inspection prep checklist",status:"Ready",meta:"Brewery floor walkthrough",severity:"default"}],activity:[{time:"07:32",summary:"Corrective action closed · Packaging deviation",badge:"CAPA"},{time:"07:05",summary:"Incident logged · Minor spill taproom",badge:"Incident"},{time:"06:49",summary:"Document updated · Allergen matrix",badge:"Docs"}],quickActions:["File Report","Assign CAPA","Download Certificates","View Policies"],quickMeta:"Compliance center shortcuts"},iam:{key:"iam",title:"Identity & Access",breadcrumb:["Mythic Tales","Governance","Identity & Access"],contexts:["Org: Mythic Tales","Review: Q2","Mode: Risk"],whatChanged:["8 access reviews due within 24 hours","MFA adoption hit 92% goal"],hero:[{label:"Active Users",value:"318",detail:"12 contractors"},{label:"Pending Access Reviews",value:"8",detail:"Owners: Dept leads",trend:"▼ 3"},{label:"MFA Adoption",value:"92%",detail:"Goal 92%",trend:"▲ 1%"}],queue:[{title:"Approve access · Taproom manager role",status:"Due Today",meta:"Request by Jamie Lee",severity:"warning"},{title:"Expiring permissions · Seasonal staff",status:"Review",meta:"7 accounts expire Friday",severity:"default"},{title:"Policy violation · Admin login anomaly",status:"Investigate",meta:"Triggered by IAM monitor",severity:"alert"}],activity:[{time:"07:47",summary:"Revoked session · Suspicious IP in EU",badge:"Security"},{time:"07:16",summary:"Role change · Added Distribution Planner role to Erin",badge:"Role"},{time:"06:53",summary:"Completed access review · Finance managers",badge:"Review"}],quickActions:["Approve Access","Generate Report","Lock Account","Open Audit Trail"],quickMeta:"Security ops shortcuts"}};var He=Object.defineProperty,je=Object.getOwnPropertyDescriptor,k=(r,e,t,i)=>{for(var a=i>1?void 0:i?je(e,t):e,s=r.length-1,o;s>=0;s--)(o=r[s])&&(a=(i?o(e,t,a):o(a))||a);return i&&a&&He(e,t,a),a};u.MtEnterpriseConsole=class extends I{constructor(){super(...arguments),this.domains=me,this.selectedDomain=L,this.density="compact",this.drawerOpen=!1,this.commandOpen=!1,this.toasts=[],this.toastId=0,this.handleKeydown=e=>{(e.metaKey||e.ctrlKey)&&e.key.toLowerCase()==="k"&&(e.preventDefault(),this.toggleCommandPalette()),e.key==="Escape"&&(this.commandOpen&&(e.preventDefault(),this.toggleCommandPalette(!1)),this.drawerOpen&&(e.preventDefault(),this.closeDrawer()))}}connectedCallback(){super.connectedCallback(),window.addEventListener("keydown",this.handleKeydown),this.selectedDomain||(this.selectedDomain=L),this.setAttribute("data-density",this.density)}disconnectedCallback(){super.disconnectedCallback(),window.removeEventListener("keydown",this.handleKeydown)}updated(){this.setAttribute("data-density",this.density)}get activeDomain(){return this.domains[this.selectedDomain]??this.domains[L]}handleNavClick(e){this.selectedDomain=e}setDensity(e){this.density=e}toggleDrawer(){this.drawerOpen=!this.drawerOpen}closeDrawer(){this.drawerOpen=!1}toggleCommandPalette(e){const t=e??!this.commandOpen;if(this.commandOpen=t,t)this.updateComplete.then(()=>{const i=this.renderRoot.querySelector("#commandSearch");i==null||i.focus()});else{const i=this.renderRoot.querySelector("#commandPaletteTrigger");i==null||i.focus()}}showToast(e){const t=++this.toastId,i={id:t,message:e};this.toasts=[...this.toasts,i],setTimeout(()=>this.dismissToast(t),2500)}dismissToast(e){const t=this.renderRoot.querySelector(`#toast-${e}`);t&&t.classList.add("toast--hide"),setTimeout(()=>{this.toasts=this.toasts.filter(i=>i.id!==e)},300)}renderNav(){return he.map(e=>m`
        <div class="nav-group" aria-labelledby="${e.id}-group">
          <h3 id="${e.id}-group">${e.label}</h3>
          <ul>
            ${e.domains.map(t=>{const i=this.domains[t],a=this.selectedDomain===t;return m`
                <li>
                  <button
                    class=${P({"nav-item":!0,active:a})}
                    data-domain=${t}
                    @click=${()=>this.handleNavClick(t)}
                    aria-current=${a?"page":"false"}
                  >
                    ${i.title}
                  </button>
                </li>
              `})}
          </ul>
        </div>
      `)}renderBreadcrumb(){const e=this.activeDomain,t=[...e.breadcrumb,e.title];return t.map((i,a)=>{const s=a===t.length-1;return m`
        <span class=${P({breadcrumb:!0,current:s})}>${i}</span>
        ${s?null:m`<span class="breadcrumb-separator">›</span>`}
      `})}renderHeroCards(){return this.activeDomain.hero.map(e=>m`
        <div class="hero-card">
          <span class="hero-card__label">${e.label}</span>
          <span class="hero-card__value">${e.value}</span>
          ${e.detail?m`<span class="hero-card__detail">${e.detail}</span>`:null}
          ${e.trend?m`<span class="hero-card__trend">${e.trend}</span>`:null}
        </div>
      `)}renderQueue(){return this.activeDomain.queue.map(e=>m`
        <li class=${P({"queue-item":!0,[`queue-item--${e.severity}`]:!0})}>
          <div class="queue-item__title">${e.title}</div>
          <div class="queue-item__meta">${e.meta}</div>
          <span class="queue-item__status">${e.status}</span>
        </li>
      `)}renderActivity(){return this.activeDomain.activity.map(e=>m`
        <li class="activity-item">
          <span class="activity-item__time">${e.time}</span>
          <div class="activity-item__summary">${e.summary}</div>
          <span class="activity-item__badge">${e.badge}</span>
        </li>
      `)}renderQuickActions(){return this.activeDomain.quickActions.map(e=>{const t=typeof e=="string"?{label:e}:e,a=(t.label??"")||"Action",s="message"in t&&t.message?t.message:`${a} queued`,o="href"in t?t.href:void 0,l=t.command;return m`
        <button
          class="quick-action"
          type="button"
          @click=${()=>{if(l){this.dispatchEvent(new CustomEvent("enterprise-command",{detail:{command:l,label:a,domain:this.selectedDomain},bubbles:!0,composed:!0}));return}if(o){window.location.href=o;return}this.showToast(s)}}
        >
          ${a}
        </button>
      `})}renderToasts(){return this.toasts.map(e=>m` <div class="toast" id=${`toast-${e.id}`}>${e.message}</div> `)}render(){const e=[{id:"compact",label:"Compact"},{id:"comfortable",label:"Comfortable"}];return m`
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
              @click=${()=>this.toggleCommandPalette()}
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
              @click=${()=>this.toggleDrawer()}
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
              ${this.activeDomain.contexts.map(t=>m`<button class="chip" type="button">${t}</button>`)}
            </div>
            <div class="density-toggle" role="group" aria-label="Density toggle">
              <span>Density</span>
              ${e.map(({id:t,label:i})=>m`
                  <button
                    type="button"
                    class=${P({"density-button":!0,active:this.density===t})}
                    data-density=${t}
                    @click=${()=>this.setDensity(t)}
                  >
                    ${i}
                  </button>
                `)}
            </div>
          </div>

          <div class="what-changed" role="status" aria-live="polite">
            ${this.activeDomain.whatChanged.map(t=>m`<span class="what-changed__item">${t}</span>`)}
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
              <div class="quick-actions__meta">${this.activeDomain.quickMeta??""}</div>
            </header>
            <div class="quick-actions__grid">${this.renderQuickActions()}</div>
          </section>
        </main>

        <aside
          id="contextDrawer"
          class=${P({"side-drawer":!0,"side-drawer--open":this.drawerOpen})}
          aria-hidden=${!this.drawerOpen}
          aria-label="Filters and context"
        >
          <header>
            <h2>Context Controls</h2>
            <button type="button" class="secondary" @click=${()=>this.closeDrawer()}>
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
          class=${P({"command-overlay":!0,"command-overlay--open":this.commandOpen})}
          role="dialog"
          aria-modal="true"
          aria-labelledby="commandOverlayTitle"
          @click=${t=>{t.target===t.currentTarget&&this.toggleCommandPalette(!1)}}
        >
          <div class="command-panel">
            <header>
              <h2 id="commandOverlayTitle">Command Palette</h2>
              <button
                type="button"
                class="secondary"
                @click=${()=>this.toggleCommandPalette(!1)}
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
    `}},u.MtEnterpriseConsole.styles=ye`
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
  `,k([J({type:Object})],u.MtEnterpriseConsole.prototype,"domains",2),k([J({type:String,attribute:"selected-domain"})],u.MtEnterpriseConsole.prototype,"selectedDomain",2),k([z()],u.MtEnterpriseConsole.prototype,"density",2),k([z()],u.MtEnterpriseConsole.prototype,"drawerOpen",2),k([z()],u.MtEnterpriseConsole.prototype,"commandOpen",2),k([z()],u.MtEnterpriseConsole.prototype,"toasts",2),u.MtEnterpriseConsole=k([Ie("mt-enterprise-console")],u.MtEnterpriseConsole),u.defaultDomain=L,u.domainGroups=he,u.enterpriseDomains=me,Object.defineProperty(u,Symbol.toStringTag,{value:"Module"})});
//# sourceMappingURL=mt-ui.umd.js.map
