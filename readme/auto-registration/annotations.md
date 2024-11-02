# Annotations

*   @NoRegistration: Makes the register ignore the provided field

    ```java
    public static final Item ITEM = new Item(new Item.Settings());
        
    @NoRegistration
    public static Item iWontBeRegistered = new Item(new Item.Settings());
    ```
*   @ModId: By default, items and blocks are registered with the minecraft namespace. Using the ModId annotation, you can give your fields a custom mod ID

    ```java
    // this will be registered as "modid:item"
    @ModId(modid = "modid")
    public static final Item ITEM = new Item(new Item.Settings());
    ```
*   @Name: Set a custom name for your field

    ```java
    // this will be registered as "minecraft:item"
    public static final Item ITEM = new Item(new Item.Settings());

    // this will be registered as "minecraft:i_have_a_custom_name"
    @Name(name = "I have a custom name")
    public static final Item ANOTHER_ITEM = new Item(new Item.Settings());
    ```
*   @NoBlockItem: Ignores BlockItem  creation for blocks

    ```java
    // this will be ignored by default since it is not a block
    public static final Item ITEM = new Item(new Item.Settings());

    public static final Block BLOCK = new Block(AbstractBlock.Settings.create());
        
    @NoBlockItem // this will not create a BlockItem for your block
    public static final Block ANOTHER_BLOCK = new Block(AbstractBlock.Settings.create());

    @BlockWithEntity // this will also register a BlockEntity for your block
    public static final Block BLOCK_WITH_ENTITY = new Block(AbstractBlock.Settings.create());
    ```

