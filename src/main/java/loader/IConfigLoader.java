package loader;

public interface IConfigLoader {

    void save(Config config, String path);

    Config getConfig(String path);
}