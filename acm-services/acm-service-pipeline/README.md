Pipeline processing support
===========================

Support for behaviour extensibility through pipeline design pattern usage.

One can customize pre- and post- entity save operations with using already available, or implementing new,
pipeline handlers. Each pipeline handler contains two methods:

* `execute()`
* `rollback()`

and can be registered as a pre-save or post-save handler with the `PipelineManager`. Handlers are registered as an
ordered list and are executed in a row. If execution of any handler (`execute()` method) fails, then full rollback
is attempted by invoking `rollback()` methods of each registered handler in reverse order.