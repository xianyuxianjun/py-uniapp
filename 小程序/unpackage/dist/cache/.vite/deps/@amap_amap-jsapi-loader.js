var __getOwnPropNames = Object.getOwnPropertyNames;
var __commonJS = (cb, mod) => function __require() {
  return mod || (0, cb[__getOwnPropNames(cb)[0]])((mod = { exports: {} }).exports, mod), mod.exports;
};

// E:/旧物回收/小程序/node_modules/@amap/amap-jsapi-loader/dist/index.js
var require_dist = __commonJS({
  "E:/旧物回收/小程序/node_modules/@amap/amap-jsapi-loader/dist/index.js"(exports, module) {
    (function(m, p) {
      "object" === typeof exports && "undefined" !== typeof module ? module.exports = p() : "function" === typeof define && define.amd ? define(p) : (m = m || self, m.AMapLoader = p());
    })(exports, function() {
      function m(a) {
        var b2 = [];
        a.AMapUI && b2.push(p(a.AMapUI));
        a.Loca && b2.push(r(a.Loca));
        return Promise.all(b2);
      }
      function p(a) {
        return new Promise(function(h, c) {
          var f = [];
          if (a.plugins)
            for (var e = 0; e < a.plugins.length; e += 1)
              -1 == d.AMapUI.plugins.indexOf(a.plugins[e]) && f.push(a.plugins[e]);
          if (g.AMapUI === b.failed)
            c("前次请求 AMapUI 失败");
          else if (g.AMapUI === b.notload) {
            g.AMapUI = b.loading;
            d.AMapUI.version = a.version || d.AMapUI.version;
            e = d.AMapUI.version;
            var l = document.body || document.head, k = document.createElement("script");
            k.type = "text/javascript";
            k.src = "https://webapi.amap.com/ui/" + e + "/main.js";
            k.onerror = function(a2) {
              g.AMapUI = b.failed;
              c("请求 AMapUI 失败");
            };
            k.onload = function() {
              g.AMapUI = b.loaded;
              if (f.length)
                window.AMapUI.loadUI(f, function() {
                  for (var a2 = 0, b2 = f.length; a2 < b2; a2++) {
                    var c2 = f[a2].split("/").slice(-1)[0];
                    window.AMapUI[c2] = arguments[a2];
                  }
                  for (h(); n.AMapUI.length; )
                    n.AMapUI.splice(0, 1)[0]();
                });
              else
                for (h(); n.AMapUI.length; )
                  n.AMapUI.splice(0, 1)[0]();
            };
            l.appendChild(k);
          } else
            g.AMapUI === b.loaded ? a.version && a.version !== d.AMapUI.version ? c("不允许多个版本 AMapUI 混用") : f.length ? window.AMapUI.loadUI(f, function() {
              for (var a2 = 0, b2 = f.length; a2 < b2; a2++) {
                var c2 = f[a2].split("/").slice(-1)[0];
                window.AMapUI[c2] = arguments[a2];
              }
              h();
            }) : h() : a.version && a.version !== d.AMapUI.version ? c("不允许多个版本 AMapUI 混用") : n.AMapUI.push(function(a2) {
              a2 ? c(a2) : f.length ? window.AMapUI.loadUI(f, function() {
                for (var a3 = 0, b2 = f.length; a3 < b2; a3++) {
                  var c2 = f[a3].split("/").slice(-1)[0];
                  window.AMapUI[c2] = arguments[a3];
                }
                h();
              }) : h();
            });
        });
      }
      function r(a) {
        return new Promise(function(h, c) {
          if (g.Loca === b.failed)
            c("前次请求 Loca 失败");
          else if (g.Loca === b.notload) {
            g.Loca = b.loading;
            d.Loca.version = a.version || d.Loca.version;
            var f = d.Loca.version, e = d.AMap.version.startsWith("2"), l = f.startsWith("2");
            if (e && !l || !e && l)
              c("JSAPI 与 Loca 版本不对应！！");
            else {
              e = d.key;
              l = document.body || document.head;
              var k = document.createElement("script");
              k.type = "text/javascript";
              k.src = "https://webapi.amap.com/loca?v=" + f + "&key=" + e;
              k.onerror = function(a2) {
                g.Loca = b.failed;
                c("请求 AMapUI 失败");
              };
              k.onload = function() {
                g.Loca = b.loaded;
                for (h(); n.Loca.length; )
                  n.Loca.splice(0, 1)[0]();
              };
              l.appendChild(k);
            }
          } else
            g.Loca === b.loaded ? a.version && a.version !== d.Loca.version ? c("不允许多个版本 Loca 混用") : h() : a.version && a.version !== d.Loca.version ? c("不允许多个版本 Loca 混用") : n.Loca.push(function(a2) {
              a2 ? c(a2) : c();
            });
        });
      }
      if (!window)
        throw Error("AMap JSAPI can only be used in Browser.");
      var b;
      (function(a) {
        a.notload = "notload";
        a.loading = "loading";
        a.loaded = "loaded";
        a.failed = "failed";
      })(b || (b = {}));
      var d = { key: "", AMap: { version: "1.4.15", plugins: [] }, AMapUI: { version: "1.1", plugins: [] }, Loca: { version: "1.3.2" } }, g = { AMap: b.notload, AMapUI: b.notload, Loca: b.notload }, n = { AMap: [], AMapUI: [], Loca: [] }, q = [], t = function(a) {
        "function" == typeof a && (g.AMap === b.loaded ? a(window.AMap) : q.push(a));
      };
      return { load: function(a) {
        return new Promise(function(h, c) {
          if (g.AMap == b.failed)
            c("");
          else if (g.AMap == b.notload) {
            var f = a.key, e = a.version, l = a.plugins;
            f ? (window.AMap && "lbs.amap.com" !== location.host && c("禁止多种API加载方式混用"), d.key = f, d.AMap.version = e || d.AMap.version, d.AMap.plugins = l || d.AMap.plugins, g.AMap = b.loading, e = document.body || document.head, window.___onAPILoaded = function(d2) {
              delete window.___onAPILoaded;
              if (d2)
                g.AMap = b.failed, c(d2);
              else
                for (g.AMap = b.loaded, m(a).then(function() {
                  h(window.AMap);
                })["catch"](c); q.length; )
                  q.splice(0, 1)[0]();
            }, l = document.createElement("script"), l.type = "text/javascript", l.src = "https://webapi.amap.com/maps?callback=___onAPILoaded&v=" + d.AMap.version + "&key=" + f + "&plugin=" + d.AMap.plugins.join(","), l.onerror = function(a2) {
              g.AMap = b.failed;
              c(a2);
            }, e.appendChild(l)) : c("请填写key");
          } else if (g.AMap == b.loaded)
            if (a.key && a.key !== d.key)
              c("多个不一致的 key");
            else if (a.version && a.version !== d.AMap.version)
              c("不允许多个版本 JSAPI 混用");
            else {
              f = [];
              if (a.plugins)
                for (e = 0; e < a.plugins.length; e += 1)
                  -1 == d.AMap.plugins.indexOf(a.plugins[e]) && f.push(a.plugins[e]);
              if (f.length)
                window.AMap.plugin(f, function() {
                  m(a).then(function() {
                    h(window.AMap);
                  })["catch"](c);
                });
              else
                m(a).then(function() {
                  h(window.AMap);
                })["catch"](c);
            }
          else if (a.key && a.key !== d.key)
            c("多个不一致的 key");
          else if (a.version && a.version !== d.AMap.version)
            c("不允许多个版本 JSAPI 混用");
          else {
            var k = [];
            if (a.plugins)
              for (e = 0; e < a.plugins.length; e += 1)
                -1 == d.AMap.plugins.indexOf(a.plugins[e]) && k.push(a.plugins[e]);
            t(function() {
              if (k.length)
                window.AMap.plugin(k, function() {
                  m(a).then(function() {
                    h(window.AMap);
                  })["catch"](c);
                });
              else
                m(a).then(function() {
                  h(window.AMap);
                })["catch"](c);
            });
          }
        });
      }, reset: function() {
        delete window.AMap;
        delete window.AMapUI;
        delete window.Loca;
        d = { key: "", AMap: { version: "1.4.15", plugins: [] }, AMapUI: { version: "1.1", plugins: [] }, Loca: { version: "1.3.2" } };
        g = {
          AMap: b.notload,
          AMapUI: b.notload,
          Loca: b.notload
        };
        n = { AMap: [], AMapUI: [], Loca: [] };
      } };
    });
  }
});
export default require_dist();
//# sourceMappingURL=@amap_amap-jsapi-loader.js.map
