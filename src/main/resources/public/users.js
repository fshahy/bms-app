/*
 * Copyright (c) 2019, 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
var server = "/";

function search (){
    var searchTerm = $("#searchText").val().trim();
    if (searchTerm != "") {
        $("#users").show();
        $("#users").html("SEARCHING...");
        $.ajax({
            url: server + "users/" +
                $("#searchType").val() + "/" +
                encodeURIComponent(searchTerm),
            method: "GET"
        }).done(
            function(data) {
                $("#users").empty();
                $("#users").hide();
                if (data.length == 0) {
                    $("#users").html("");
                    $("#notFound").show();
                    $("#notFound").html("No users found matching your search criteria");
                } else {
                    showResults(data);
                }
                $("#users").show(400, "swing");
            });
    } else {
        loadUsers();
    }
}

$(function() {   
    $("#searchText").on("keyup", function(e) {
        if (e.keyCode == 13) {
            search ();
        }
    });
});

function showResults(data){
    $("#users").hide();
    $("#users").empty();
    $("#notFound").hide();
    data.forEach(function(user) {
        var item = $(renderUsers(user));
        item.on("click", function() {
            var detailItem = $(renderDetailUser(user));
            $("#home").hide();
            $("#detail").empty();                                   
            $("#notFound").hide();
            $("#detail").append(detailItem);
            $("#users").hide(
                400,
                "swing",
                function() {
                    $("#detail").show()
                });
        });
        $("#users").append(item);
    });
}

function showUserForm() {
    $("#notFound").hide();
    $("#editForm").hide();
    $("#deleteButton").hide();
    $("#userForm").show();
    $("#formTitle").text("Add User");
    $("#home").hide();
    $("#users").hide();
}

function loadUsers() {
    $("#notFound").hide();
    $("#searchText").val("");
    $("#userForm").hide();
    $("#editForm").hide();
    $("#home").show();
    $("#users").show();
    $("#users").html("LOADING...");
    $.ajax({
        dataType: "json",
        url: server + "users",
        method: "GET"
    }).done(function(data) {
        showResults(data); 
        $("#users").show(400, "swing");
    });
}


function renderUsers(user){
    var template = $('#users_tpl').html();
    Mustache.parse(template);
    var rendered = Mustache.render(template, {
        "firstName" : user.firstName,
        "lastName" : user.lastName,
        "email" : user.email
    });
    return rendered;
}

function renderDetailUser(user){
    var template = $('#detail_tpl').html();
    Mustache.parse(template);
    var rendered = Mustache.render(template,{
        "id" : user.id,
        "firstName" : user.firstName,
        "lastName" : user.lastName,
        "email" : user.email
    });
    return rendered;
}

function save() {
    var user = {
        id: "",
        firstName: $("#firstName").val(),
        lastName: $("#lastName").val(),
        email: $("#email").val()
    };
    $.ajax({
        url: server + "users",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify(user)
    }).done(function(data) {
        $("#detail").hide();
        $("#firstName").val("");
        $("#lastName").val("");
        $("#email").val("");
        loadUsers();
    });

}

function updateUser() {
    var user = {
        id: $("#editId").val(),
        firstName: $("#editFirstName").val(),
        lastName: $("#editLastName").val(),
        email: $("#editEmail").val(),
    };
    $("#detail").html("UPDATING...");
    $.ajax({
        url: server + "users/" + user.id,
        method: "PUT",
        contentType: "application/json",
        data: JSON.stringify(user)
    }).done(function(data) {
        $("#detail").hide();
        loadUsers();
    });
}

function deleteUser() {
    var user = {
        firstName: $("#editFirstName").val(),
        lastName: $("#editLastName").val(),
        id: $("#editId").val()
    };
    $('<div></div>').dialog({
        modal: true,
        title: "Confirm Delete",
        open: function() {
            var markup = 'Are you sure you want to delete ' +
                user.firstName + ' ' + user.lastName +
                " user?";
            $(this).html(markup);
        },
        buttons: {
            Ok: function() {
                $("#detail").html("DELETING...");
                $(this).dialog("close");
                $.ajax({
                    url: server + "users/" + user.id,
                    method: "DELETE"
                }).done(function(data) {
                    $("#detail").hide();
                    loadUsers();
                });
            },
            Cancel: function() {
                $(this).dialog("close");
            }
        }
    });

}
