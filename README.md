# Epigraph

Under construction

- [Roadmap](roadmap.md)
- [Todo](todo.md)
- [IDEA plugin snapshot](https://github.com/SumoLogic/epigraph/files/1181584/epigraph-idea-plugin-0.0.4.zip)


### First-time Maven build bootstrap:
- In project root folder run: `./mvnw clean install -Plight-psi`
- Build the rest normally, e.g.: `./mvnw clean test`


### IntelliJ IDEA project set-up:
- In project root folder run: `./mvnw clean install -Plight-psi,main`
  (installs current development version of Epigraph Maven plugin(s) to make these available to IDEA builds)
- In IDEA menu: `File` > `Open...` > Select project root folder
