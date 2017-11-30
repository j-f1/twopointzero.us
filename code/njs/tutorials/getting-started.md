All n.js notifications appear in a “Notification Center,”
represented by the `<notification-center>` element.

To get started, add a `<notification-center>` element to your HTML page:
```HTML
<notification-center></notification-center>
```

Next, include the library (in the `<head>` element):

```HTML
<!-- If you’re supporting browsers that don’t support
     Custom Elements (http://caniuse.com/#feat=shadowdom,
     as of 2016, you should be), include this polyfill: -->
<script src="//cdnjs.cloudflare.com/ajax/libs/webcomponentsjs/0.7.21/webcomponents.min.js" charset="utf-8"></script>
<link rel="stylesheet" href="/path/to/n.css" charset="utf-8">
<script src="/path/to/n.js" charset="utf-8"></script>
```
In case you haven’t seen that first tag before, it’s a HTML import, kind of like a `<script>` tag or a `<link rel="stylesheet">` tag, except it imports HTML and separates it from the rest of your page, so it doesn’t affect you.

To show notifications to the user,
call `addNotification` on a notification center element.
To start, get a reference to the notification center:
```JavaScript
// Method 1
var center = document.querySelector('notification-center');
// Method 2 (requires jQuery)
var center = $('notification-center');
```
Next, call the `addNotification` method with your options:
```JavaScript
var notification = center.addNotification({
  title: "My Title",
  text: "My Notification!",
  actions: {
    "Cool!": true,
    "Keep": false
  }
});
```
This will cause the notification to appear. You can change any attribute
of the returned notification object, and it will automatically update:
```JavaScript
notification.title = "New Title!";
```
