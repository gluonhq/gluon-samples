<!--

    Copyright (c) 2017, Gluon
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
        * Neither the name of Gluon, any associated website, nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
    ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
    WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
    DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
    DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
    (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
    LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
    ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

-->
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" class="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="description" content="Gluon CloudLink demo for Java EE - Message of the Day">
        <meta name="title" content="Gluon Java EE MOTD">
        <title>Java EE Message of the Day</title>

        <link rel="stylesheet" type="text/css" href="/motd-server/webjars/bootstrap/3.3.7-1/css/bootstrap.min.css">
    </head>

    <body>
        <div class="container">
            <div id="body" class="row">
                <div class="page-header">
                    <h1>Message of the Day</h1>
                    <h4>An application demonstrating Gluon CloudLink and Java EE.</h4>
                </div>
                <div id="messages" class="row hide">
                    <div role="alert">
                        <button type="button" class="close" data-hide="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <div id="messages_content"></div>
                    </div>
                </div>
                <div class="row">
                    <form id="add-form" class="form-inline">
                        <div class="form-group">
                            <input type="text" class="form-control" placeholder="Type a message" value="${model}" name="motd" id="motd" style="width: 100%;"/>
                        </div>
                        <button type="button" class="btn btn-default" id="add-item">Update Message</button>
                    </form>
                </div>
            </div>
        </div>
        <script type="text/javascript" src="/motd-server/webjars/jquery/3.1.1/jquery.min.js"></script>
        <script type="text/javascript" src="/motd-server/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js"></script>
        <script type="text/javascript">
            $(function() {
                $('#add-item').click(function(e) {
                    $.post("/motd-server/jaxrs/front", { motd: $("#motd").val()}, function(data) {
                        $('#messages_content').html('<h4>Message updated: ' + data + '</h4>');
                        $('#messages').children().first().addClass('alert alert-success alert-dismissable');
                        $('#messages').removeClass('hide').slideDown().show();

                        e.preventDefault();
                    });
                });
                $("[data-hide]").on("click", function(){
                    $(this).closest("." + $(this).attr("data-hide")).hide();
                });
            });
        </script>
    </body>
</html>
