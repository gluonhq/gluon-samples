var observableModule = require("data/observable");
var CommentsModel = require("../../model/comments-model");

var timer = require("timer");

var comments = new CommentsModel([]);
var pageData = new observableModule.fromObject({
    comments: comments
})

function onNavigatingTo(args) {
    var page = args.object;
    page.bindingContext = pageData;

    comments.empty();
    const id = timer.setTimeout(() => {
        comments.load();
    }, 2000);
}

exports.onNavigatingTo = onNavigatingTo;