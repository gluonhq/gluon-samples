const CryptoJS = require('crypto-js');
const OAuth = require('oauth-1.0a');

var fs = require("file-system");
var fetchModule = require("fetch");
var qs = require("qs");
var Observable = require("data/observable").Observable;
var ObservableArray = require("data/observable-array").ObservableArray;

const commentsIdentifier = "comments";
var config = fs.knownFolders.currentApp().getFile("gluoncloudlink_config.json");

var oauth;
var gluonConfig = {};
config.readText()
    .then(function(content) {
        var jsonConfig = JSON.parse(content);

        gluonConfig = {
            host: (jsonConfig.hasOwnProperty("host") ? jsonConfig.host : "https://cloud.gluonhq.com"),
            gluonCredentials: {
                applicationKey: jsonConfig.gluonCredentials.applicationKey,
                applicationSecret: jsonConfig.gluonCredentials.applicationSecret
            }
        }

        oauth = OAuth({
            consumer: { key: gluonConfig.gluonCredentials.applicationKey, secret: gluonConfig.gluonCredentials.applicationSecret },
            signature_method: 'HMAC-SHA1',
            hash_function(base_string, key) {
                return CryptoJS.HmacSHA1(base_string, key).toString(CryptoJS.enc.Base64);
            }
        });
    }, function(error) {
        throw new Error("Could not read Gluon CloudLink configuration file: " + error);
    }
);

function CommentsModel(items) {
    var viewModel = new ObservableArray(items);

    viewModel.load = function() {
        var requestData = {
            url: gluonConfig.host + "/3/data/client/retrieveList",
            method: "POST",
            data: {"identifier":commentsIdentifier}
        };
        var oauthHeaders = oauth.toHeader(oauth.authorize(requestData));
        var headers = Object.assign(oauthHeaders, {
            "Accept-Encoding": "gzip",
            "Content-Type": "application/x-www-form-urlencoded"
        });
        return fetch(requestData.url, {
            method: requestData.method,
            body: qs.stringify(requestData.data),
            headers: headers
        })
        .then(handleErrors)
        .then(function(response) {
            return response.json();
        }).then(function(data) {
            console.info(JSON.stringify(data));
            data.payload.forEach(function(obj) {
                var comment = JSON.parse(obj.payload);
                viewModel.push(comment);
            })
        });
    };

    viewModel.empty = function() {
        while (viewModel.length) {
            viewModel.pop();
        }
    };
    
    return viewModel;
}

function handleErrors(response) {
    if (!response.ok) {
        console.info(JSON.stringify(response));
        throw Error(response.statusText);
    }
    return response;
}

module.exports = CommentsModel;
