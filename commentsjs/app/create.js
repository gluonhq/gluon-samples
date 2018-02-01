var frameModule = require("ui/frame");
var observableModule = require("data/observable");
var CommentModel = require("./comment-model");

var comment = new CommentModel();
var pageData = new observableModule.fromObject({
    comment: comment
});

function onNavigatingTo(args) {
    var page = args.object;
    page.bindingContext = pageData;
    console.info("PAGEDATA: " + JSON.stringify(pageData));
}

function onSubmit(args) {
    comment.save(function() {
        frameModule.topmost().navigate("./comments");
    });
}

function onCancel(args) {
    frameModule.topmost().goBack();
}

exports.onNavigatingTo = onNavigatingTo;
exports.onSubmit = onSubmit;
exports.onCancel = onCancel;