'use strict';

angular.module('bluelatex.Shared.Controllers.Messages', ['bluelatex.Shared.Services.Messages'])
  .controller('MessagesController', ['$rootScope', '$scope', 'MessagesService','$log',
    function ($rootScope, $scope, MessagesService, $log) {
      // give access to messages, warnings and errors
      $scope.messages = MessagesService.messages;
      $scope.warnings = MessagesService.warnings;
      $scope.errors = MessagesService.errors;

      $scope.messagesSession = MessagesService.messagesSession;
      $scope.warningsSession = MessagesService.warningsSession;
      $scope.errorsSession = MessagesService.errorsSession;

      $scope.close = MessagesService.close;
    }
  ]);