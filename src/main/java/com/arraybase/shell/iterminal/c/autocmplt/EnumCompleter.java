/*
 * Copyright (c) 2002-2012, the original author or authors.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.arraybase.shell.iterminal.c.autocmplt;

import com.arraybase.shell.iterminal.c.interal.Preconditions;

/**
 * {@link Completer} for {@link Enum} names.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class EnumCompleter
    extends StringsCompleter
{
    public EnumCompleter(Class<? extends Enum> source) {
        Preconditions.checkNotNull(source);

        for (Enum<?> n : source.getEnumConstants()) {
            this.getStrings().add(n.name().toLowerCase());
        }
    }
}