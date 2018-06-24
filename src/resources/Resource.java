package resources;

public abstract class Resource
{
    public abstract String getName();
    public abstract String getPath();
    
    @Override
    public String toString()
    {
        return this.getName();
    }
}
