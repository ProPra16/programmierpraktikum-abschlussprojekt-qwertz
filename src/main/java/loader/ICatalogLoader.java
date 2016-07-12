package loader;

import Attd.Catalog;
import Attd.CatalogItem;

import java.util.List;

public interface ICatalogLoader {

    List<Catalog> getCatalogs(String path);

    void save(List<Catalog> catalogs, String path);

    CatalogItem getRawCatalogItem(String path, Catalog catalog, String version);
}