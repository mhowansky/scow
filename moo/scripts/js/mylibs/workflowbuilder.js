// Generated by CoffeeScript 1.7.1
(function() {
  var Activities, Activity, ActivityFactory, Decision, Exit, HumanTask, Loop, PrettyPrintVisitor, ScriptTask, ServiceTask, Signal, Subprocess, Workflow, WorkflowBuilderViewModel, WorkflowXmlConverter, createDisplay, dndOptions,
    __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; },
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  $(function() {
    return ko.applyBindings(new WorkflowBuilderViewModel());
  });

  dndOptions = {
    autoExpandMS: 100,
    preventVoidMoves: true,
    preventRecursiveMoves: true,
    dragStart: function(node) {
      return node.data.draggable;
    },
    dragEnter: function(target, data) {
      return target.data.self.dragEnter(data);
    },
    dragDrop: function(target, data) {
      if (data.hitMode === "over") {
        return data.otherNode.moveTo(target.getFirstChild(), "before");
      } else {
        return data.otherNode.moveTo(target, data.hitMode);
      }
    }
  };

  WorkflowBuilderViewModel = (function() {
    function WorkflowBuilderViewModel() {
      this.save = __bind(this.save, this);
      this.prettyPrint = __bind(this.prettyPrint, this);
      this.configTree = __bind(this.configTree, this);
      this.loadWorkflow = __bind(this.loadWorkflow, this);
      this.workflow = ko.observable();
      this.loadWorkflow("v2-simple");
      this.selectedActivity = ko.observable();
    }

    WorkflowBuilderViewModel.prototype.loadWorkflow = function(workflowName) {
      return COW.cowRequest("processes/" + workflowName).done((function(_this) {
        return function(data) {
          _this.data = data;
          _this.workflow(new Workflow(data));
          return _this.configTree(_this.workflow());
        };
      })(this));
    };

    WorkflowBuilderViewModel.prototype.configTree = function(workflow) {
      $("#tree").fancytree({
        extensions: ["dnd"],
        debugLevel: 2,
        source: [workflow],
        imagePath: "images/",
        icons: false,
        dnd: dndOptions,
        click: (function(_this) {
          return function(event, data) {
            return _this.selectedActivity(data.node.data.self);
          };
        })(this)
      });
      return this.tree = $("#tree").fancytree("getTree");
    };

    WorkflowBuilderViewModel.prototype.prettyPrint = function() {
      return new PrettyPrintVisitor(this.tree);
    };

    WorkflowBuilderViewModel.prototype.save = function() {
      var converter, xml;
      converter = new WorkflowXmlConverter(this.tree);
      xml = converter.getXml();
      return COW.xmlRequest("processes/" + converter.key, "put", xml).done(function(d) {
        return alert("xml saved");
      });
    };

    return WorkflowBuilderViewModel;

  })();

  createDisplay = function(label, value, inputType) {
    if (inputType == null) {
      inputType = "text";
    }
    return {
      label: ko.observable(label + ":"),
      value: ko.observable(value),
      inputType: ko.observable(inputType)
    };
  };

  WorkflowXmlConverter = (function() {
    function WorkflowXmlConverter(tree) {
      this.visitHumanTask = __bind(this.visitHumanTask, this);
      this.visitActivities = __bind(this.visitActivities, this);
      this.visitWorkflow = __bind(this.visitWorkflow, this);
      var workflowRoot;
      this.xml = $($.parseXML('<process xmlns="http://www.wiredwidgets.org/cow/server/schema/model-v2"></process>'));
      this.xmlPosition = this.xml;
      workflowRoot = tree.rootNode.children[0];
      console.log(this.xml);
      this.visit(workflowRoot);
    }

    WorkflowXmlConverter.prototype.getXml = function() {
      return this.xml[0];
    };

    WorkflowXmlConverter.prototype.visit = function(node) {
      return node.data.self.accept(this, node);
    };

    WorkflowXmlConverter.prototype.visitWorkflow = function(node) {
      var after, process, workflow;
      workflow = node.data.self;
      process = $(this.xmlPosition.find("process"));
      this.key = workflow.key;
      process.attr("name", workflow.key);
      process.attr("key", workflow.key);
      after = this.moveXmlPosition(this, process);
      this.visit(node.children[0]);
      return after();
    };

    WorkflowXmlConverter.prototype.moveXmlPosition = function(self, newXmlPosition) {
      var oldPosition, _ref;
      _ref = [self.xmlPosition, newXmlPosition], oldPosition = _ref[0], self.xmlPosition = _ref[1];
      return function() {
        return self.xmlPosition = oldPosition;
      };
    };

    WorkflowXmlConverter.prototype.visitActivities = function(node) {
      var activities, after, child, xmlActivities, _i, _len, _ref;
      activities = node.data.self;
      xmlActivities = $("<activities />").appendTo(this.xmlPosition);
      xmlActivities.attr("sequential", activities.isSequential);
      after = this.moveXmlPosition(this, xmlActivities);
      _ref = node.children;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        child = _ref[_i];
        this.visit(child);
      }
      return after();
    };

    WorkflowXmlConverter.prototype.visitHumanTask = function(node) {
      var task, xmlTask;
      task = node.data.self;
      xmlTask = $("<task />").appendTo(this.xmlPosition);
      xmlTask.attr("name", task.name);
      this.createTextNode(xmlTask, "description", task.description);
      this.createTextNode(xmlTask, "assignee", task.assignee);
      return this.createTextNode(xmlTask, "candidateGroups", task.candidateGroups);
    };

    WorkflowXmlConverter.prototype.visitServiceTask = function(node) {
      return console.log(node);
    };

    WorkflowXmlConverter.prototype.visitScript = function(node) {
      return console.log(node);
    };

    WorkflowXmlConverter.prototype.visitDecision = function(node) {
      return console.log(node);
    };

    WorkflowXmlConverter.prototype.visitExit = function(node) {
      return console.log(node);
    };

    WorkflowXmlConverter.prototype.visitLoop = function(node) {
      return console.log(node);
    };

    WorkflowXmlConverter.prototype.visitSignal = function(node) {
      return console.log(node);
    };

    WorkflowXmlConverter.prototype.visitSubprocess = function(node) {
      return console.log(node);
    };

    WorkflowXmlConverter.prototype.createTextNode = function(target, tag, content) {
      return $("<" + tag + ">" + content + "</" + tag + ">").appendTo(target);
    };

    return WorkflowXmlConverter;

  })();

  PrettyPrintVisitor = (function() {
    function PrettyPrintVisitor(tree) {
      var workflowRoot;
      this.tree = tree;
      this.dedent = __bind(this.dedent, this);
      this.indent = __bind(this.indent, this);
      this.tabs = "";
      workflowRoot = this.tree.rootNode.children[0];
      this.visit(workflowRoot);
    }

    PrettyPrintVisitor.prototype.display = function(title) {
      return console.log(this.tabs + title);
    };

    PrettyPrintVisitor.prototype.indent = function() {
      return this.tabs += "\t";
    };

    PrettyPrintVisitor.prototype.dedent = function() {
      return this.tabs = this.tabs.substr(1);
    };

    PrettyPrintVisitor.prototype.visit = function(node) {
      return node.data.self.accept(this, node);
    };

    PrettyPrintVisitor.prototype.visitChildren = function(node) {
      var child, _i, _len, _ref;
      this.indent();
      _ref = node.children;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        child = _ref[_i];
        this.visit(child);
      }
      return this.dedent();
    };

    PrettyPrintVisitor.prototype.visitWorkflow = function(node) {
      this.display(node.key);
      return this.visitChildren(node);
    };

    PrettyPrintVisitor.prototype.visitActivities = function(node) {
      this.display(node.title);
      return this.visitChildren(node);
    };

    PrettyPrintVisitor.prototype.visitHumanTask = function(node) {
      return this.display(node.title);
    };

    PrettyPrintVisitor.prototype.visitServiceTask = function(node) {
      return this.display(node.title);
    };

    PrettyPrintVisitor.prototype.visitScript = function(node) {
      return this.display(node.title);
    };

    PrettyPrintVisitor.prototype.visitDecision = function(node) {
      this.display(node.title);
      return this.visitChildren(node);
    };

    PrettyPrintVisitor.prototype.visitExit = function(node) {
      return this.display(node.title);
    };

    PrettyPrintVisitor.prototype.visitLoop = function(node) {
      this.display(node.title);
      return this.visitChildren(node);
    };

    PrettyPrintVisitor.prototype.visitSignal = function(node) {
      return this.display(node.title);
    };

    PrettyPrintVisitor.prototype.visitSubprocess = function(node) {
      return this.display(node.title);
    };

    return PrettyPrintVisitor;

  })();

  Activity = (function() {
    function Activity(data) {
      var _ref;
      this.data = data;
      this.dragEnter = __bind(this.dragEnter, this);
      this.title = (_ref = this.data.name) != null ? _ref : this.constructor.name;
      this.name = this.title;
      this.key = this.data.key;
      this.icon = "Icon_Task.png";
      this.draggable = true;
      this.expanded = true;
      this.self = this;
      this.isDecision = false;
      this.isActivities = false;
      this.folder = false;
      this.description = this.data.description;
    }

    Activity.prototype.dragEnter = function() {
      if (this.folder) {
        return ["over"];
      }
      return ["before", "after"];
    };

    return Activity;

  })();

  Workflow = (function(_super) {
    __extends(Workflow, _super);

    function Workflow(data) {
      this.data = data;
      this.key = this.data.key;
      this.title = "<span class='glyphicon glyphicon-list-alt'></span> " + this.key;
      this.children = [ActivityFactory.create(this.data.activity)];
      this.draggable = false;
      this.folder = true;
      this.expanded = true;
      this.self = this;
    }

    Workflow.prototype.dragEnter = function() {
      return false;
    };

    Workflow.prototype.accept = function(visitor, node) {
      return visitor.visitWorkflow(node);
    };

    return Workflow;

  })(Activity);

  Activities = (function(_super) {
    __extends(Activities, _super);

    Activities.typeString = "org.wiredwidgets.cow.server.api.model.v2.Activities";

    function Activities(data) {
      var d;
      this.data = data;
      this.dragEnter = __bind(this.dragEnter, this);
      Activities.__super__.constructor.call(this, this.data);
      this.isSequential = this.data.sequential;
      if (this.data.name != null) {
        this.title = this.data.name;
      } else {
        this.title = this.isSequential ? "List" : "Parallel List";
      }
      this.children = (function() {
        var _i, _len, _ref, _results;
        _ref = this.data.activity;
        _results = [];
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          d = _ref[_i];
          _results.push(ActivityFactory.create(d));
        }
        return _results;
      }).call(this);
      this.icon = "Icon_List.png";
      this.folder = true;
      this.isActivities = true;
    }

    Activities.prototype.dragEnter = function(data) {
      var _ref;
      if (((_ref = data.node.getParent()) != null ? _ref.data.self.isDecision : void 0) && data.otherNode.data.self.isActivities) {
        return ["over", "after", "before"];
      } else {
        return ["over"];
      }
    };

    Activities.prototype.accept = function(visitor, node) {
      return visitor.visitActivities(node);
    };

    Activities.prototype.typeStr = function() {
      return Activities.typeString;
    };

    return Activities;

  })(Activity);

  HumanTask = (function(_super) {
    __extends(HumanTask, _super);

    HumanTask.typeString = "org.wiredwidgets.cow.server.api.model.v2.Task";

    function HumanTask(data) {
      this.data = data;
      HumanTask.__super__.constructor.call(this, this.data);
      this.assignee = data.assignee;
      this.candidateGroups = data.candidateGroups;
    }

    HumanTask.prototype.accept = function(visitor, node) {
      return visitor.visitHumanTask(node);
    };

    HumanTask.prototype.typeStr = function() {
      return HumanTask.typeString;
    };

    return HumanTask;

  })(Activity);

  ServiceTask = (function(_super) {
    __extends(ServiceTask, _super);

    ServiceTask.typeString = "org.wiredwidgets.cow.server.api.model.v2.ServiceTask";

    function ServiceTask(data) {
      this.data = data;
      ServiceTask.__super__.constructor.call(this, this.data);
      this.icon = "Icon_ServiceTask.png";
    }

    ServiceTask.prototype.accept = function(visitor, node) {
      return visitor.visitServiceTask(node);
    };

    ServiceTask.prototype.typeStr = function() {
      return ServiceTask.typeString;
    };

    return ServiceTask;

  })(Activity);

  ScriptTask = (function(_super) {
    __extends(ScriptTask, _super);

    ScriptTask.typeString = "org.wiredwidgets.cow.server.api.model.v2.Script";

    function ScriptTask(data) {
      ScriptTask.__super__.constructor.apply(this, arguments);
      this.icon = "Icon_Script.png";
    }

    ScriptTask.prototype.accept = function(visitor, node) {
      return visitor.visitScript(node);
    };

    ScriptTask.prototype.typeStr = function() {
      return ScriptTask.typeString;
    };

    return ScriptTask;

  })(Activity);

  Decision = (function(_super) {
    __extends(Decision, _super);

    Decision.typeString = "org.wiredwidgets.cow.server.api.model.v2.Decision";

    function Decision(data) {
      this.dragEnter = __bind(this.dragEnter, this);
      var c, option, _i, _len, _ref;
      Decision.__super__.constructor.apply(this, arguments);
      this.children = (function() {
        var _i, _len, _ref, _results;
        _ref = data.option;
        _results = [];
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          option = _ref[_i];
          _results.push(ActivityFactory.create(option.activity));
        }
        return _results;
      })();
      _ref = this.children;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        c = _ref[_i];
        c.icon = "Icon_Decision_Arrow.png";
      }
      this.icon = "Icon_Decision.png";
      this.folder = true;
      this.isDecision = true;
    }

    Decision.prototype.dragEnter = function(data) {
      if (data.otherNode.data.self.isActivities) {
        return ["over"];
      } else {
        return false;
      }
    };

    Decision.prototype.accept = function(visitor, node) {
      return visitor.visitDecision(node);
    };

    Decision.prototype.typeStr = function() {
      return Decision.typeString;
    };

    return Decision;

  })(Activity);

  Exit = (function(_super) {
    __extends(Exit, _super);

    Exit.typeString = "org.wiredwidgets.cow.server.api.model.v2.Exit";

    function Exit(data) {
      Exit.__super__.constructor.call(this, data);
      this.icon = "Icon_Exit.png";
    }

    Exit.prototype.accept = function(visitor, node) {
      return visitor.visitExit(node);
    };

    Exit.prototype.typeStr = function() {
      return Exit.typeString;
    };

    return Exit;

  })(Activity);

  Signal = (function(_super) {
    __extends(Signal, _super);

    Signal.typeString = "org.wiredwidgets.cow.server.api.model.v2.Signal";

    function Signal(data) {
      Signal.__super__.constructor.call(this, data);
      this.icon = "Icon_Signal.png";
    }

    Signal.prototype.accept = function(visitor, node) {
      return visitor.visitSignal(node);
    };

    Signal.prototype.typeStr = function() {
      return Signal.typeString;
    };

    return Signal;

  })(Activity);

  Subprocess = (function(_super) {
    __extends(Subprocess, _super);

    Subprocess.typeString = "org.wiredwidgets.cow.server.api.model.v2.SubProcess";

    function Subprocess(data) {
      Subprocess.__super__.constructor.call(this, data);
      this.icon = "Icon_SubProcess.png";
    }

    Subprocess.prototype.accept = function(visitor, node) {
      return visitor.visitSubprocess(node);
    };

    Subprocess.prototype.typeStr = function() {
      return Subprocess.typeString;
    };

    return Subprocess;

  })(Activity);

  Loop = (function(_super) {
    __extends(Loop, _super);

    Loop.typeString = "org.wiredwidgets.cow.server.api.model.v2.Loop";

    function Loop(data) {
      Loop.__super__.constructor.apply(this, arguments);
      this.children = ActivityFactory.create(data.activity).children;
      this.icon = "Icon_Loop.png";
      this.folder = true;
    }

    Loop.prototype.accept = function(visitor, node) {
      return visitor.visitLoop(node);
    };

    Loop.prototype.typeStr = function() {
      return Loop.typeString;
    };

    return Loop;

  })(Activity);

  ActivityFactory = (function() {
    function ActivityFactory() {}

    ActivityFactory.typeMap = {};

    ActivityFactory.typeMap[Activities.typeString] = Activities;

    ActivityFactory.typeMap[HumanTask.typeString] = HumanTask;

    ActivityFactory.typeMap[ServiceTask.typeString] = ServiceTask;

    ActivityFactory.typeMap[ScriptTask.typeString] = ScriptTask;

    ActivityFactory.typeMap[Decision.typeString] = Decision;

    ActivityFactory.typeMap[Exit.typeString] = Exit;

    ActivityFactory.typeMap[Loop.typeString] = Loop;

    ActivityFactory.typeMap[Signal.typeString] = Signal;

    ActivityFactory.create = function(data) {
      return new this.typeMap[data.declaredType](data.value);
    };

    return ActivityFactory;

  })();

}).call(this);

//# sourceMappingURL=workflowbuilder.map
