# MC-Language

Minecraft Language is a compiler from a custom language `mcl/mclang` to Minecraft's datapack functions `mcfunction`.

Example:
```toml
package example {

    @Load
    func test() {
        run(`say @p "Hello, World!"`)
    }
}
```
#### The above will produce the following output:
 + A `loaders.mcfunction` file with the content:
   ```
    function projectid:example/test.mcfunction
    ```
 + A `example/test.mcfunction` file with the content:
   ```
    say @p "Hello, World!"
   ```
   
It is expected by the person using this to manually add loaders.mcfunction to the load tag.