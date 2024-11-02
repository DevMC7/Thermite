---
description: Make a registry for your custom type
---

# Custom Registries

Making a custom registry using thermite is simple

1.  Make your custom Register interface. This will be implemented by other classes that contain your custom types that need to be registered\


    ```java
    @Register(YourType.class)
    public class CustomRegister {

    }
    ```
2.  Create your registry. This will be used to register Register\<T> classes. You can find implementations of Registry in the net.devmc.thermite.lib.registration.registries package

    ```java
    public class CustomRegistry implements Registry<CustomRegister> {
        @Override
        public void register(Class<?> clazz) {
           
        }

        @Override
        public void registerAll(List<Class<?>> registers) {

        }

        @Override
        public void registerAll(Class<?>... registers) {

        }

        @Override
        @ApiStatus.Internal
        public void init() {

        }
    }
    ```

