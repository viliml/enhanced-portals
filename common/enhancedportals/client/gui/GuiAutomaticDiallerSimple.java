package enhancedportals.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import enhancedportals.lib.Localization;
import enhancedportals.lib.Reference;
import enhancedportals.lib.Strings;
import enhancedportals.network.packet.PacketAutomaticDiallerUpdate;
import enhancedportals.network.packet.PacketEnhancedPortals;
import enhancedportals.tileentity.TileEntityAutomaticDialler;

public class GuiAutomaticDiallerSimple extends GuiNetwork
{
    int guiTop = 0, guiLeft = 0, xSize = 176, ySize = 166, elementCount = 0;
    RenderItem itemRenderer = new RenderItem();
    TileEntityAutomaticDialler dialler;

    List<GuiGlyphElement> elementList = new ArrayList<GuiGlyphElement>();
    List<GuiGlyphElement> stackList = new ArrayList<GuiGlyphElement>();

    public GuiAutomaticDiallerSimple(TileEntityAutomaticDialler autoDial)
    {
        dialler = autoDial;

        for (int i = 0; i < 9; i++)
        {
            elementList.add(new GuiGlyphElement(guiLeft + 8 + i * 18, guiTop + 15, Reference.glyphItems.get(i).getItemName().replace("item.", ""), Reference.glyphItems.get(i), this));
        }

        for (int i = 9; i < 18; i++)
        {
            elementList.add(new GuiGlyphElement(guiLeft + 8 + (i - 9) * 18, guiTop + 33, Reference.glyphItems.get(i).getItemName().replace("item.", ""), Reference.glyphItems.get(i), this));
        }

        for (int i = 18; i < 27; i++)
        {
            elementList.add(new GuiGlyphElement(guiLeft + 8 + (i - 18) * 18, guiTop + 51, Reference.glyphItems.get(i).getItemName().replace("item.", ""), Reference.glyphItems.get(i), this));
        }

        if (!dialler.activeNetwork.equals(""))
        {
            String[] split = dialler.activeNetwork.split(Reference.glyphSeperator);

            for (String element2 : split)
            {
                for (int j = 0; j < Reference.glyphItems.size(); j++)
                {
                    if (Reference.glyphItems.get(j).getItemName().replace("item.", "").equalsIgnoreCase(element2))
                    {
                        GuiGlyphElement element = elementList.get(j);

                        stackList.add(new GuiGlyphElement(0, 0, Reference.glyphItems.get(j).getItemName().replace("item.", ""), Reference.glyphItems.get(j), this, true));
                        element.stackSize++;
                        elementCount++;
                    }
                }
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 1)
        {
            String str = "";

            for (int i = 0; i < stackList.size(); i++)
            {
                str = str + Reference.glyphSeperator + stackList.get(i).value;
            }

            if (str.length() > 0)
            {
                str = str.substring(Reference.glyphSeperator.length());
            }

            dialler.activeNetwork = str;
            FMLClientHandler.instance().getClient().thePlayer.closeScreen();
            PacketDispatcher.sendPacketToServer(PacketEnhancedPortals.makePacket(new PacketAutomaticDiallerUpdate(dialler)));
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void drawScreen(int x, int y, float par3)
    {
        drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.func_110577_a(new ResourceLocation(Reference.RESOURCE_NAME, Reference.GUI_LOCATION + "portalModifierNetwork.png"));
        int x2 = (width - xSize) / 2;
        int y2 = (height - ySize) / 2 - 3;
        drawTexturedModalRect(x2, y2, 0, 0, 166 + 22, 176);
        fontRenderer.drawString(Localization.localizeString("tile.automaticDialler.name"), guiLeft + xSize / 2 - fontRenderer.getStringWidth(Localization.localizeString("tile.automaticDialler.name")) / 2, guiTop - 15, 0xFFCCCCCC);

        fontRenderer.drawString(Strings.Glyphs.toString(), guiLeft + 7, guiTop + 3, 0xFF444444);
        fontRenderer.drawString(Strings.UniqueIdentifier.toString(), guiLeft + 7, guiTop + 71, 0xFF444444);

        super.drawScreen(x, y, par3); // Draw buttons

        // Draw glyphs
        for (int i = stackList.size() - 1; i >= 0; i--)
        {
            stackList.get(i).drawElement(guiLeft + 8 + i * 18, guiTop + 83, x, y, fontRenderer, itemRenderer, mc.renderEngine);
        }

        for (int i = 8; i >= 0; i--)
        {
            elementList.get(i).drawElement(guiLeft, guiTop, x, y, fontRenderer, itemRenderer, mc.renderEngine);
        }

        for (int i = 17; i >= 9; i--)
        {
            elementList.get(i).drawElement(guiLeft, guiTop, x, y, fontRenderer, itemRenderer, mc.renderEngine);
        }

        for (int i = elementList.size() - 1; i >= 18; i--)
        {
            elementList.get(i).drawElement(guiLeft, guiTop, x, y, fontRenderer, itemRenderer, mc.renderEngine);
        }
    }

    @Override
    public void elementClicked(GuiGlyphElement element, int button)
    {
        if (!element.isStack)
        {
            if (button == 0)
            {
                if (elementCount < 9)
                {
                    stackList.add(new GuiGlyphElement(0, 0, element.value, element.itemStack, this, true));
                    element.stackSize++;

                    elementCount++;
                }
            }
            else if (button == 1)
            {
                if (elementCount > 0)
                {
                    for (int i = stackList.size() - 1; i >= 0; i--)
                    {
                        if (stackList.get(i).itemStack.isItemEqual(element.itemStack))
                        {
                            stackList.remove(i);

                            if (element.stackSize >= 1)
                            {
                                element.stackSize--;
                            }

                            elementCount--;
                            return;
                        }
                    }
                }
            }
        }
        else
        {
            if (button == 1)
            {
                stackList.remove(element);

                for (int i = elementList.size() - 1; i >= 0; i--)
                {
                    if (elementList.get(i).itemStack.isItemEqual(element.itemStack))
                    {
                        if (elementList.get(i).stackSize >= 1)
                        {
                            elementList.get(i).stackSize--;
                        }

                        elementCount--;
                        return;
                    }
                }

                elementCount--;
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        super.initGui();

        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;

        buttonList.add(new GuiButton(1, guiLeft + 13, guiTop + 107, 150, 20, Strings.Save.toString()));
        ((GuiButton) buttonList.get(0)).enabled = !stackList.isEmpty();
    }

    @Override
    protected void keyTyped(char par1, int par2)
    {
        super.keyTyped(par1, par2);

        if (par2 == mc.gameSettings.keyBindInventory.keyCode)
        {
            mc.thePlayer.closeScreen();
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int buttonClicked)
    {
        super.mouseClicked(x, y, buttonClicked);

        for (int i = 0; i < elementList.size(); i++)
        {
            elementList.get(i).handleMouseClick(guiLeft, guiTop, x, y, buttonClicked);
        }

        for (int i = 0; i < stackList.size(); i++)
        {
            stackList.get(i).handleMouseClick(guiLeft + 8 + i * 18, guiTop + 83, x, y, buttonClicked);
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if (!stackList.isEmpty())
        {
            ((GuiButton) buttonList.get(0)).enabled = true;
        }
    }
}
