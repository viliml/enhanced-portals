package uk.co.shadeddimensions.enhancedportals.item;

import net.minecraft.item.Item;
import uk.co.shadeddimensions.enhancedportals.EnhancedPortals;

public class ItemEP2 extends Item
{
    public ItemEP2(int par1, boolean tab)
    {
        super(par1);

        if (tab)
        {
            setCreativeTab(EnhancedPortals.creativeTab);
        }
    }
}
