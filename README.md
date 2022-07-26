# MC-Language

Minecraft Language is a compiler from a custom language `mcl/mclang` to Minecraft's datapack functions `mcfunction`.

Example:
```swift
package example {

    @Load
    func test() {
        run(`say @p "Hello, World!"`)
    }
}
```
#### The above will produce the following output:
 + A `loaders.mcfunction` file with the content:
   ```mcfunction
    function projectid:example/test.mcfunction
    ```
 + A `example/test.mcfunction` file with the content:
   ```mcfunction
    say @p "Hello, World!"
   ```
   
It is expected by the person using this to manually add loaders.mcfunction to the load tag.

As this is just a thing I made in a day I am not sure if I will work on it that much but it is here for anyone to use.
Some stuff I may add or am willing to accept a PR for are:

- [ ] Add for the ability to call functions from the same package without having to use the full path.
- [ ] Add shorthand default functions for things like `print()` and such, Note: This is why `COMMA` token exists.
- [ ] Add Wiki for all the things available.
- [ ] Automatically add the `loaders.mcfunction` file to the load tag.
- [ ] Automatically add the `tickers.mcfunction` file to the tick tag.
- [ ] Add while loops.
- [ ] Add breaks to loops.
- [ ] Cache tickers and loaders into a tag. (Note: this will be harder for selector ticks.)
- [ ] Add else statements. (Note: this is possible by having an intermediate function where if ran it will set a score to 1 and by default 0 so if its 0 then the else should run)