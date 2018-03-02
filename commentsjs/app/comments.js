var frameModule = require("ui/frame");
var observableModule = require("data/observable");
var CommentsModel = require("./comments-model");

var comments = new CommentsModel([]);
var pageData = new observableModule.fromObject({
    comments: comments
});

function onNavigatingTo(args) {
    var page = args.object;
    page.bindingContext = pageData;

    comments.empty();
    comments.load();
}

function onCreate(args) {
    frameModule.topmost().navigate("create");
}

exports.onNavigatingTo = onNavigatingTo;
exports.onCreate = onCreate;