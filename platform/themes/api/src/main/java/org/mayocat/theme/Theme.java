package org.mayocat.theme;

import java.nio.file.Path;

import com.google.common.base.Objects;

public class Theme
{
    public enum Type
    {
        FILE_SYSTEM,
        CLASSPATH
    }

    // The type of theme (where it's retrieved from)
    private Type type;

    // The path of the theme, relative to the root of the "permanent directory" (a.k.a. data dir).
    // For example:
    // * themes/globalTheme/theTheme
    // * tenants/myTenant/themes/localTheme/myTheme
    private Path path;

    // The definition of this theme (name, description, addons, etc.)
    private ThemeDefinition definition;

    // The parent this theme inherits from
    private Theme parent;

    // Is this a tenant "own" theme (a theme in the tenant theme directory)
    private boolean isTenantOwnTheme = false;

    public Theme(Path path, ThemeDefinition definition)
    {
        this(path, definition, null, Type.FILE_SYSTEM);
    }

    public Theme(Path path, ThemeDefinition definition, Type type)
    {
        this(path, definition, null, type);
    }

    public Theme(Path path, ThemeDefinition definition, Theme parent)
    {
        this(path, definition, parent, Type.FILE_SYSTEM);
    }

    public Theme(Path path, ThemeDefinition definition, Theme parent, Type type)
    {
        this.path = path;
        this.definition = definition;
        this.parent = parent;
        this.type = type;
    }

    public Path getPath()
    {
        return path;
    }

    public ThemeDefinition getDefinition()
    {
        if (definition != null) {
            return definition;
        }
        if (parent != null) {
            return parent.getDefinition();
        }
        return null;
    }

    public Theme getParent()
    {
        return parent;
    }

    public boolean isTenantOwnTheme()
    {
        return isTenantOwnTheme;
    }

    public void setTenantOwnTheme(boolean tenantOwnTheme)
    {
        this.isTenantOwnTheme = tenantOwnTheme;
    }

    public Type getType()
    {
        return type;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(
                this.path,
                this.parent
        );
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        final Theme other = (Theme) o;

        return Objects.equal(this.path, other.path)
                && Objects.equal(this.parent, other.parent);
    }
}
