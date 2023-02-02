# MC-Language

Minecraft Language is a compiler from a custom language `mcl/mclang` to Minecraft's datapack functions `mcfunction`.

Example:
```swift
package example {

    @Event("minecraft:load")
    func test() {
        // Produces output 'Hello, World!' in chat
        hello(input = "world")
        // Produces output 'Hello, default!' in chat
        hello()
    }
    
    macro hello(input: string = "default")
      -> "say Hello, ${input}!";
}
```
#### The above will produce the following output:
 + A `load.json` file with the content:
   ```json
    {
       "values": [
         "pack:example/test"
       ]
    }
    ```
 + A `example/test.mcfunction` file with the content:
   ```mcfunction
    say Hello, World!
    say Hello, default!
   ```