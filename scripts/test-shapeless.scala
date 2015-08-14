import shapeless._
import shapeless.Lens

case class Foo(name:String, id:Int)

def fooLens(lensId: Lens[Foo, Int])(foo:Foo) = println(lensId.get(foo))

val foo = Foo("John", 1)
val idLens = lens[Foo].id

fooLens(lens[Foo] >> 'id)(foo)
fooLens(idLens)(foo)

