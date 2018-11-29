jQuery.fn.extend({
    drap: function (opts) {
        var _self = this, _this = $(this), posX = 0, posY = 0;
        opts = jQuery.extend({
            drapMove: null,
            isLimit: false,
            callback: function () { }
        }, opts || {});
        _self.mousemove = function (e) {
            e.stopPropagation();
            if ($.browser.msie) { e = window.event; }
            var x = e.clientX - posX;
            var y = e.clientY - posY;
            if (opts.isLimit) {
                if (x < 0) {
                    x = 0;
                }
                if (y < 0) {
                    y = 0;
                }
                if (x > ($(document).width() - _this.width() - 2)) {
                    x = ($(document).width() - _this.width() - 2);
                }
                if (y > ($(document).height() - _this.height() - 2)) {
                    y = ($(document).height() - _this.height() - 2);
                }
            }
            _this.css("left", x + "px");
            _this.css("top", y + "px");
        }
        _this.find(opts.drapMove).mousedown(function (e) {
            e.stopPropagation();
            if ($.browser.msie) { e = window.event; }
            posX = e.clientX - parseInt(_this.offset().left);
            posY = e.clientY - parseInt(_this.offset().top);
            $(document).mousemove(function (ev) {
                _self.mousemove(ev);
            });
        });
        $(document).mouseup(function () {
            $(document).unbind("mousemove");
            opts.callback();
        });
        _this.find(opts.drapMove).css("cursor", "move");
    }
});