# Basic Rules for Codebase

Please, follow these rules to keep the code clean, readable, and speak-for-itself.  
Any changes are allowed but let’s discuss them with the team.

#### **Extensions**

If possible, should be written for all places that bloat the code and are often duplicated.

It is also worth keeping them in one place since we have the Utils module, it is worth keeping them in the same place, and not scattering them
throughout the project.

And we divide them logically into different files. No need to make one general.  
`FragmentExtension.kt`  
`FlowExtensions.Kt`  
`ActivityExtensions.kt`

####     

**Interfaces**

We follow the principles of **SOLID** and Architecture with common sense.  
`Repositories`, `Use Cases`, etc. We first write the interface and work exclusively through it.  
There are places in the project where there are interfaces, but the work goes directly with the object. This should be fixed.

You don't need to do this for `ViewModels`.

####     

**try-catch**

If we write `try-catch`, or we see someone else's ... then we burn out all `Throwable ignored` .  
If there is no need to process - use `Timber`. We don't ignore it.  
And we also use a more adequate type of errors.  
Not just a `Throwable`.

####     

**Double exclamation marks (!!)**

We don't use Kotlin to break its rules.  
This is an exceptional approach and should be avoided. And be very careful when automatically converting Java to Kotlin.

We write it only if this is a direct indication, for example with `ViewBinding` ... in other cases, we process it with a check.  
`if (null)` or `?.` at your discretion.

If this is old code, then throw the error yourself.  
`?: throw SomeException("understandable text")`

This approach will be more explicit and indicate the error. And also more customizable, if it is necessary to make not a system error, but our type of
error.

####     

**Deprecated**

If you find it and there is an opportunity to change it, then we either create a task or fix it right away if it concerns the current task.  
Facebook may again be an exception, it does not have _"simple"_ in its vocabulary, so according to the situation.

####     

**Clean code**

When we write code, we remove all Errors and Warnings before committing.

The studio illuminates everything for us **(top right corner)**. We keep the code clean.  
If there is no way to fix it now or at all, then we either do `@Suppress` or `//noinspection`. And comment on why.

For example  
`@Suppress("unused", "Default constructor is required by ...")`  
`constructor() : this(true)`

Who uses Git in the studio, there are checkmarks when you make a commit:  
Refactor, Rearrangement, Optimize Imports, Code Analysis.

Who does not use, then follows himself and does it manually.  
Use **ctrl+alt+L** (⌘⌥L)

####     

**Method / "fun" splits**

We do not write everything in one method. Uncle Bob once said that even if there are more than 2 lines of code, we can already take it out. But it's
hardcore. It's ok if there are 10 or 20, but readable.

`initView() { initRecycler()
initToolbar()
init..... }`

`rx.observe(createBlabla())
.map(mapToBloBlo())
....(20 more operations)
.subscribe(processBloResult, handleError)`

####     

**Javadoc / Comments**

If the method performs a complex/rare function or is simply not readable, not just a `list.forEach`, then, if possible, we write documentation for the
class and method. It doesn't matter how clear it is to you what this method does.

The company can always hire new people. Or we can all leave, and there will be no one to tell what is happening in the method.

For example. The `fun saveUser()` can not only save the user inside but also access the server and map anything.

If you were stuck for half an hour thinking what **NOT YOUR** method did, then the same scheme. We help the whole team not to waste time.

And you need to write with meaning.  
Bad example from the project:

`
**Set control top margin
*@param top **
void setTop(int top);`

param top - what is it, pixel or dp or some int constant? Or is it measured in fingers? two fingers from the top?

####     

**Naming**

We write so that it would be clear, and not "**KINDA** understandable."  
If we initialize something, then we write that initSomething, and not just init.  
handleSomthing, processSomthing, provide.... and any synonym that suits you best.

`processUserList`  
`handleServerError`  
`compressPhoto`

Also **SOLID**. **S**. Your fun shouldn't do everything at once.

Variables, no 1-2 letters. Not `i`, but `index`. Not `LD`, but `LiveData`.  
The exception is generics.

`xml` files are also made understandable. Not `pr_setting`, but `fragment_setting`, or if you really need it, then `fragment_pr_setting`.  
`activity_....`  
`view_...`  
No customizations from you.

####     

**Codestyle**

A common codestyle for the entire team has already been added to the project and added to `.gitignor`.

If there are suggestions or inconveniences, all this can be customized and changed. But discuss with a team.

####     

**The end**

And in the end, I can say that let's **not** rely too much on the principle _"it works - don't touch it"_.  
Being on the same level with the whole community and pumping your skills on new technologies, not spoiling your mood with bad code, helping new
teammates, speeding up our app, etc. is quite a good goal.