$(function() {
// required to be implemented by subclassers:
  // `generateList(firstNumbers, secondNumbers)` generates all possible facts using the two sets of numbers.
  // `fillFact($el, fact)` fills the fact.


function choice(list) {
  // from http://stackoverflow.com/a/4435017/5244995
  var randomIndex = Math.floor(Math.random() * list.length);
  return list[randomIndex];
}

function areChecked(selector) {
  // `selector` selects an <input> with a <label> as a parent.
  var result = [];
  $(selector).each(function () {
    if ($(this).is(":checked")) {
      result.push($(this).parent().text());
    }
  });
  return result;
}

$('.update').click(function (event) {
  var firstNumbers = areChecked('form .first input'),
  secondNumbers = areChecked('form .second input'),
  allFacts;
  $('table .operator').text($('form .operator select').val());

  allFacts = generateList(firstNumbers, secondNumbers);
  $('.worksheet>tr,.worksheet>tbody>tr').children('td').each(function () {
    var fact = choice(allFacts);
    allFacts.splice(allFacts.indexOf(fact), 1); // remove fact
    fillFact($(this), fact);
    if (!allFacts.length) {
      if ($('.repeats').is(':checked')) {
        allFacts = generateList(firstNumbers, secondNumbers);
      } else {
        alert("There aren’t enough possible facts to fill the page. Please allow more numbers to generate this worksheet.");
        return false;
      }
    }
  });
  event.preventDefault();
  return false;
});
$('*').on('keyup mouseup', function () {
  $("br:first-child:last-child").remove();
});
});
