const CryptoJS = require('crypto-js');
const OAuth = require('oauth-1.0a');

var fs = require("file-system");
var fetchModule = require("fetch");
// var http = require("http");
var qs = require("qs");
var Observable = require("data/observable").Observable;
var ObservableArray = require("data/observable-array").ObservableArray;

var config = fs.knownFolders.currentApp().getFile("gluoncloudlink-config.json");

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
                console.info("base_string = " + base_string + ", key = " + key);
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
            data: {"identifier":"comments"}
        };
        console.info("URL:" + requestData.url);
        console.info(requestData.data);
        console.info(requestData.method);
        var oauthHeaders = oauth.toHeader(oauth.authorize(requestData));
        var headers = Object.assign(oauthHeaders, {
            "Accept": "application/json",
            "Accept-Encoding": "gzip",
            "Content-Type": "application/x-www-form-urlencoded"
        });
        console.info("HEADER: " + JSON.stringify(headers));
        // return http.request({
        //     url: requestData.url,
        //     method: requestData.method,
        //     headers: headers,
        //     content: requestData.data
        // }).then(function(response) {
        //     console.info(response.statusCode);
        //     console.info(response.content.toJSON());
        // }, function(e) {
        //     console.info("Error: " + e);
        // });
        return fetch(requestData.url, {
            method: requestData.method,
            body: qs.stringify(requestData.data),
            headers: headers
        })
        .then(handleErrors)
        .then(function(response) {
            console.info("response.json: " + response.json());
            return response.json();
        }).then(function(data) {
            data.Result.forEach(function(comment) {
                viewModel.comments.push({
                    comment: comment.comment,
                    author: comment.author
                })
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