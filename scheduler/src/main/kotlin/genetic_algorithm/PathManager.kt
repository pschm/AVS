package genetic_algorithm

import java.util.*

class PathManager {
    companion object {
        // Holds our products
        private val destinationProducts = ArrayList<Product>()

        /**
         * Adds a destination product
         * @param product
         */
        fun addProduct(product: Product) {
            destinationProducts.add(product)
        }

        /**
         * Get a product
         * @param index
         * @return Product
         */
        fun getProduct(index: Int): Product? {
            return destinationProducts[index]
        }

        /**
         * Get the number of destination products
         * @return Number of Products
         */
        fun numberOfProducts(): Int {
            return destinationProducts.size
        }

        fun clear() {
            destinationProducts.clear()
        }
    }
}