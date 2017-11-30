document.addEventListener('DOMContentLoaded', function() {
  if (window.moment && window.$ && window.$.fn.checkbox && window.$.fn.modal && window.$.fn.velocity) {
    $('.loader').remove();
    setTimeout(function () {
      $('main').show();
    }, 500);
    window.maynt = false; // don’t display the may row.
    moment.defaultFormat = "h:mm";
    moment.prototype.natural = function () {
      var secs = this.diff(moment(), 'seconds', true);
      if (secs < 0.5) {
        return "now";
      } else if (secs <= 11) {
        return "in " + parseInt(secs + 1) + "…";
      } else {
        return this.fromNow();
      }
    };
    $('input').on('keydown', function(event) {
      if (event.which === 13) {
        $('.modal').find('.positive.button').click();
      }
    });
    $('.checkbox').checkbox({
      onUnchecked: function() {
        window.maynt = true;
        $('.maymay, .maymay *').addClass('disabled');
      },
      onChecked: function() {
        window.maynt = false;
        $('.maymay, .maymay *').removeClass('disabled');
      }
    });
    $('.setup.modal').modal({
      closable: false,
      onApprove: function() {
        var start = moment();
        var may = start.clone().add($('.may.h').val(), 'h').add($('.may.m').val(), 'm');
        var stop = start.clone().add($('.must.h').val(), 'h').add($('.must.m').val(), 'm');
        var fullDuration = stop.diff(start, 'seconds');
        var mayDuration = may.diff(start, 'seconds');
        $('.start time').text(start.format());
        $('.stop time').text(stop.format());
        if (maynt) {
          $('body').addClass('maynt');
        }

        var update = function() {
          $('main>.bar').css('width', (moment().diff(start, 'seconds') / fullDuration) * 100 + '%');
          $('.must time').text(stop.natural());
          $('.may time').text(may.natural());
          $('.now time').text(moment().format());
          if (moment().isAfter(stop)) {
            clearInterval(timer);
            $('.big, .may').velocity({height: '0vh', opacity: 0});
            $('.must').velocity({height: '100vh'})
                      .children('.small').velocity("fadeOut")
                      .end().children('.large').velocity('fadeIn');
          }
          if (!moment().isAfter(may)) {
            $('main .may .bar').css('width', (moment().diff(start, 'seconds') / mayDuration) * 100 + '%');
          } else {
            $('main .may .bar').velocity({"backgroundColor": '#F3FFF4', 'width': '100%'});
          }
        };
        update();
        var timer = setInterval(update, 1000);
        $('main').fullScreen(true);
      }
    }).modal('show');
  } else {
    $('body').addClass('bad').html('<h1 class="ui icon header"><i class="lightning icon"></i>Noooo!</h1><h3>Something didn’t load right. Try reloading or upgrading your browser.</h3>');
  }
});
