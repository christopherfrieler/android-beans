package rocks.frieler.android.beans

interface BeanReference<T : Any> {
	fun use() : T
}
