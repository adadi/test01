$(function () {
    function e(e) {
        for (var t = [{re: /[\xC0-\xC6]/g, ch: "A"}, {re: /[\xE0-\xE6]/g, ch: "a"}, {
            re: /[\xC8-\xCB]/g,
            ch: "E"
        }, {re: /[\xE8-\xEB]/g, ch: "e"}, {re: /[\xCC-\xCF]/g, ch: "I"}, {
            re: /[\xEC-\xEF]/g,
            ch: "i"
        }, {re: /[\xD2-\xD6]/g, ch: "O"}, {re: /[\xF2-\xF6]/g, ch: "o"}, {
            re: /[\xD9-\xDC]/g,
            ch: "U"
        }, {re: /[\xF9-\xFC]/g, ch: "u"}, {re: /[\xD1]/g, ch: "N"}, {
            re: /[\xF1]/g,
            ch: "n"
        }], a = 0, r = t.length; r > a; a++) e = e.replace(t[a].re, t[a].ch);
        return e
    }

    $("#arti_quoi").focus(), $(".suggestion").click(function () {
        return $("#" + $(this).data("tofill")).val($(this).data("link")).blur(), $("#" + $(this).data("fill")).val($(this).data("label")).blur().change(), $("#" + $(this).data("tofill")).change(), $("#arti_ou").focus(), $("html, body").animate({scrollTop: 0}, "slow"), "" != $("#url_region").val() && "" != $("#url_metier").val() && $("#form_recherche").submit(), $("#url_region").val() || $("#arti_ou").next(".bloc_helper").show(), !1
    }), $.widget("custom.catcomplete", $.ui.autocomplete, {
        _renderMenu: function (e, t) {
            var a = !0, r = this, i = "";
            $.each(t, function (t, n) {
                n.c != i && (class_plus = a ? "noborder" : "", a = !1, i = n.c), r._renderItemData(e, n)
            })
        }
    }), $("#arti_quoi, #arti_ou").on("keyup change", function () {
        "" != $(this).val() && $(this).val() != $(this).attr("placeholder") ? $(".reset", $(this).parent()).removeClass("off") : $(".reset", $(this).parent()).addClass("off")
    }), $("#arti_quoi, #arti_ou, #url_metier, #url_region").on("change focus keyup", function () {
        "block" == $(this).parents("span").find(".bloc_helper").css("display") && $(this).parents("span").find(".bloc_helper").fadeOut()
    }), $("#form_recherche").on("submit", function () {
        var e = !0;
        return "" == $("#arti_quoi").val() ? ($("#url_metier").parents("span").find(".bloc_helper").fadeIn(), e = !1) : "" == $("#arti_ou").val() && ($("#url_region").parents("span").find(".bloc_helper").fadeIn(), e = !1), e
    }), $(".reset.btn_autocomplete").on("click", function () {
        $(this).addClass("off"), $(this).parent().find("input").each(function () {
            $(this).val("")
        })
    });
    var t = {}, r = {};
    $("#arti_quoi").catcomplete({
        autoFocus: !0, delay: 25, source: function (e, a) {
            var i = e.term;
            return i in t ? void a(t[i]) : void $.post("/index/metier", {req: e.term}, function (e) {
                0 == e.res.length && r ? t[i] = e.res : (r = t[i] = e.res, a(e.res))
            }, "json")
        }, select: function (e, t) {
            return "no_result" == t.item.cclass ? !1 : ("artisan" == t.item.cclass ? document.location.href = t.item.id : $("#url_metier").val("prestation" == t.item.cclass ? "_" + t.item.idd : t.item.idd), $(this).val(t.item.value))
        }, minLength: 2
    });
    var i = {}, n = {}, s = "";
    $("#arti_ou").autocomplete({
        autoFocus: !0, delay: 25, source: function (e, t) {
            var a = e.term;
            return a in i ? void t(i[a]) : void $.post("/index/ville", {req: e.term}, function (e) {
                var r = $.map(e.res, function (e) {
                    var t = e.label;
                    return "" != e.n && (t += " (" + e.n + ")"), {label: t, value: t, id: e.id}
                });
                0 == e.res.length && n ? i[a] = r : (n = i[a] = r, t(r))
            }, "json")
        }, select: function (e, t) {
            return $(this).val(t.item.value), $("#url_region").val(t.item.id), !1
        }, minLength: 2
    }), $.ui.autocomplete.prototype._renderItem = function (t, r) {
        var i = this.term;
        i = e(i);
        var n = $.trim(i).split(/\s+/), l = "", c = r.label, o = e(c), u = n.length;
        $.each(n, function (t, a) {
            var r = o.replace(/<.+?>/g, ""), i = o.split(new RegExp(a, "gi").exec(r)), n = 0;
            for (var s in i) {
                var h = i[s], d = c.substr(n, h.length);
                if (l += d, n += d.length, s < i.length - 1) {
                    var $ = c.substr(n, a.length);
                    l += "" == $ ? $ : "<b>" + $ + "</b>", n += a.length
                }
            }
            o = e(l), c = l, t != u - 1 && (l = "")
        });
        var h = /\(([^)]+)\)/;
        return l = l.replace(h, "<SPAN style='font-style:italic!important;font-size:0.9em'>($1)</SPAN>"), href = "artisan" == r.cclass ? "href='" + r.id + "'" : "", "artisan" == r.cclass || "prestation" == r.cclass ? (ville = "artisan" == r.cclass ? "<span class='ss'>" + r.ville + "</span>" : "<span class='ss'>" + r.metier + "</span>", div = "<span>" + l + ville + "</span>") : div = l, a = $("<li></li>").data("item.autocomplete", r).addClass(r.cclass).append("<a " + href + " >" + div + "</a>").appendTo(t), s != r.c && (s = r.c, a.addClass(r.cclass)), "artisan" == r.cclass && a.addClass("Si"), a
    }


    $("#bt_arti_quoi").focus(), $(".suggestion").click(function () {
        return $("#" + $(this).data("tofill")).val($(this).data("link")).blur(), $("#" + $(this).data("fill")).val($(this).data("label")).blur().change(), $("#" + $(this).data("tofill")).change(), $("#bt_arti_ou").focus(), $("html, body").animate({scrollTop: 0}, "slow"), "" != $("#bt_url_region").val() && "" != $("#bt_url_metier").val() && $("#bt_form_recherche").submit(), $("#bt_url_region").val() || $("#bt_arti_ou").next(".bloc_helper").show(), !1
    }), $.widget("custom.catcomplete", $.ui.autocomplete, {
        _renderMenu: function (e, t) {
            var a = !0, r = this, i = "";
            $.each(t, function (t, n) {
                n.c != i && (class_plus = a ? "noborder" : "", a = !1, i = n.c), r._renderItemData(e, n)
            })
        }
    }), $("#bt_arti_quoi, #bt_arti_ou").on("keyup change", function () {
        "" != $(this).val() && $(this).val() != $(this).attr("placeholder") ? $(".reset", $(this).parent()).removeClass("off") : $(".reset", $(this).parent()).addClass("off")
    }), $("#bt_arti_quoi, #bt_arti_ou, #bt_url_metier, #bt_url_region").on("change focus keyup", function () {
        "block" == $(this).parents("span").find(".bloc_helper").css("display") && $(this).parents("span").find(".bloc_helper").fadeOut()
    }), $("#bt_form_recherche").on("submit", function () {
        var e = !0;
        return "" == $("#bt_arti_quoi").val() ? ($("#bt_url_metier").parents("span").find(".bloc_helper").fadeIn(), e = !1) : "" == $("#bt_arti_ou").val() && ($("#bt_url_region").parents("span").find(".bloc_helper").fadeIn(), e = !1), e
    }), $(".reset.btn_autocomplete").on("click", function () {
        $(this).addClass("off"), $(this).parent().find("input").each(function () {
            $(this).val("")
        })
    });
    var t = {}, r = {};
    $("#bt_arti_quoi").catcomplete({
        autoFocus: !0, delay: 25, source: function (e, a) {
            var i = e.term;
            return i in t ? void a(t[i]) : void $.post("/index/metier", {req: e.term}, function (e) {
                0 == e.res.length && r ? t[i] = e.res : (r = t[i] = e.res, a(e.res))
            }, "json")
        }, select: function (e, t) {
            return "no_result" == t.item.cclass ?
                !1 : ("artisan" == t.item.cclass ?
                    document.location.href = t.item.id :
                    $("#bt_url_metier").val("prestation" == t.item.cclass ?
                        "_" + t.item.idd : t.item.idd), $(this).val(t.item.value))
        }, minLength: 2
    });

    console.log(t);

    var i = {}, n = {}, s = "";
    $("#bt_arti_ou").autocomplete({
        autoFocus: !0, delay: 25, source: function (e, t) {
            var a = e.term;
            return a in i ? void t(i[a]) : void $.post("/index/ville", {req: e.term}, function (e) {
                var r = $.map(e.res, function (e) {
                    var t = e.label;
                    return "" != e.n && (t += " (" + e.n + ")"), {label: t, value: t, id: e.id}
                });
                0 == e.res.length && n ? i[a] = r : (n = i[a] = r, t(r))
            }, "json")
        }, select: function (e, t) {
            return $(this).val(t.item.value), $("#bt_url_region").val(t.item.id), !1
        }, minLength: 2
    }), $.ui.autocomplete.prototype._renderItem = function (t, r) {
        var i = this.term;
        i = e(i);
        var n = $.trim(i).split(/\s+/), l = "", c = r.label, o = e(c), u = n.length;
        $.each(n, function (t, a) {
            var r = o.replace(/<.+?>/g, ""), i = o.split(new RegExp(a, "gi").exec(r)), n = 0;
            for (var s in i) {
                var h = i[s], d = c.substr(n, h.length);
                if (l += d, n += d.length, s < i.length - 1) {
                    var $ = c.substr(n, a.length);
                    l += "" == $ ? $ : "<b>" + $ + "</b>", n += a.length
                }
            }
            o = e(l), c = l, t != u - 1 && (l = "")
        });
        var h = /\(([^)]+)\)/;


        //console.log(href);
        //console.log(r.cclass);
        //console.log(l);
        // l = l.replace(h, "<SPAN style='font-style:italic!important;font-size:0.9em'>($1)</SPAN>");
        // //console.log(l);
        // if ("artisan" == r.cclass) {
        //     href = "href='" + r.id + "'";
        // } else {
        //     href = "";
        // }
        // //console.log(href);
        // if ("artisan" == r.cclass || "prestation" == r.cclass) {
        //     if ("artisan" == r.cclass) {
        //         ville = "<span class='ss'>" + r.ville + "</span>";
        //     } else {
        //         ville = "<span class='ss'>" + r.metier + "</span>";
        //         div = "<span>" + l + ville + "</span>";
        //     }
        // } else {
        //     div = l;
        // }
        // if ("artisan" == r.cclass){
        //     a = $("<li></li>")
        //         .data("item.autocomplete", r)
        //         .addClass("bt_artisan").append("<a " + href + " >" + div + "</a>")
        //         .appendTo(t);
        // }
        // else{
        // a = $("<li></li>")
        //     .data("item.autocomplete", r)
        //     .addClass(r.cclass).append("<a class='bt_autocomplete'" + href + " >" + div + "</a>")
        //     .appendTo(t);
        // }
        // if (s != r.c) {
        //     s = r.c;
        // } else {
        //     a.addClass(r.cclass);
        // }
        // if ("artisan" == r.cclass) {
        //     a.addClass("Si");
        // }
        //
        // console.log(a.prop('outerHTML'));


        return l = l.replace(h, "<SPAN style='font-style:italic!important;font-size:0.9em'>($1)</SPAN>")
            , href = "artisan" == r.cclass ? "href='" + r.id + "'" : ""
            , "artisan" == r.cclass || "prestation" == r.cclass
            ? (ville = "artisan" == r.cclass
                ? "<span class='ss bt_ss2'>" + r.ville + "</span>"
                : "<span class='ss bt_ss2'>" + r.metier + "</span>"
                , div = "<span class='ss bt_ss1' >" + l + ville + "</span>")
            : div = l, a = $("<li class='bt_margin4'></li>")
            .data("item.autocomplete", r)
            .addClass(r.cclass).append("<a class='bt_autocomplete'" + href + " >" + div + "</a>")
            .appendTo(t), s != r.c && (s = r.c, a.addClass(r.cclass))
            , "artisan" == r.cclass && a.addClass("Si"), a
    }
})
;