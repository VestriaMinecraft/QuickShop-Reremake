/*
 * This file is a part of project QuickShop, the name is ChatSheetPrinter.java
 *  Copyright (C) PotatoCraft Studio and contributors
 *
 *  This program is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.maxgamer.quickshop.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.maxgamer.quickshop.QuickShop;


@AllArgsConstructor
@Getter
@Setter
/*
 A utils for print sheet on chat.
*/
public class ChatSheetPrinter {
    private final CommandSender p;

    public void printCenterLine(@NotNull String text) {
        if (!text.isEmpty()) {
            MsgUtil.sendDirectMessage(p,
                    ChatColor.DARK_PURPLE
                            + MsgUtil.getMessage("tableformat.left_half_line", p)
                            + text
                            + MsgUtil.getMessage("tableformat.right_half_line", p));
        }
    }

    public void printExecutableCmdLine(
            @NotNull String text, @NotNull String hoverText, @NotNull String executeCmd) {
        QuickShop.getInstance().getQuickChat().sendExecutableChat(p, text, hoverText, executeCmd);
    }

    public void printFooter() {
        MsgUtil.sendDirectMessage(p, ChatColor.DARK_PURPLE + MsgUtil.getMessage("tableformat.full_line", p));
    }

    public void printHeader() {
        MsgUtil.sendDirectMessage(p, ChatColor.DARK_PURPLE + MsgUtil.getMessage("tableformat.full_line", p));
    }

    public void printLine(@NotNull String text) {
        String[] texts = text.split("\n");
        for (String str : texts) {
            if (!str.isEmpty()) {
                MsgUtil.sendDirectMessage(p, ChatColor.DARK_PURPLE + MsgUtil.getMessage("tableformat.left_begin", p) + str);
            }
        }
    }

    public void printSuggestedCmdLine(
            @NotNull String text, @NotNull String hoverText, @NotNull String suggestCmd) {
        QuickShop.getInstance().getQuickChat().sendSuggestedChat(p, text, hoverText, suggestCmd);

    }

}
